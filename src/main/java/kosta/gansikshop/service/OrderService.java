package kosta.gansikshop.service;

import kosta.gansikshop.domain.*;
import kosta.gansikshop.dto.order.*;
import kosta.gansikshop.exception.NotEnoughStockException;
import kosta.gansikshop.repository.cart.CartRepository;
import kosta.gansikshop.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final EntityValidationService entityValidationService;

    /** 장바구니에서 결제 */
    @Transactional
    public void processOrder(Long memberId, List<OrderItemRequestDto> orderItems) {
        // Member 조회 및 유효성 검사
        Member member = entityValidationService.validateMember(memberId);

        // 장바구니 아이템 조회
        List<Cart> cartItems = cartRepository.findCartDetails(memberId, Optional.empty());
        Map<Long, Cart> cartItemMap = cartItems.stream()
                .collect(Collectors.toMap(cart -> cart.getItem().getId(), cart -> cart));

        // 주문 아이템 생성 및 유효성 검증
        List<OrderItem> orderItemList = orderItems.stream()
                .map(orderItemDto -> {
                    // 아이템 존재 여부 확인
                    Item item = entityValidationService.validateItem(orderItemDto.getItemId());

                    // 장바구니에 아이템이 있는지 확인
                    Cart cart = cartItemMap.get(orderItemDto.getItemId());
                    if (cart == null) {
                        throw new IllegalArgumentException(item.getName() + "이 장바구니에 없습니다.");
                    }

                    // 재고 수량 확인
                    if (item.getStockQuantity() < orderItemDto.getCount()) {
                        throw new NotEnoughStockException(item.getName() + "의 현재 수량은 " + item.getStockQuantity() + "개 입니다.");
                    }

                    return OrderItem.createOrderItem(item, item.getName(), item.getPrice(), orderItemDto.getCount());
                })
                .collect(Collectors.toList());

        // Order 생성
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        // 장바구니에서 주문된 아이템 삭제
        List<Long> itemIds = orderItems.stream().map(OrderItemRequestDto::getItemId).collect(Collectors.toList());
        cartRepository.deleteAllByMemberIdAndItemIdIn(memberId, itemIds);
    }

    /** 즉시 주문 */
    @Transactional
    public void saveOrder(Long memberId, OrderRequestDto requestDto) {
        // Member 조회 및 유효성 검사
        Member member = entityValidationService.validateMember(memberId);

        // OrderItem 생성 및 유효성 검사
        List<OrderItem> orderItems = requestDto.getOrderItems().stream()
                .map(orderItemDto -> {
                    Item item = entityValidationService.validateItem(orderItemDto.getItemId());

                    if (item.getStockQuantity() < orderItemDto.getCount()) {
                        throw new NotEnoughStockException(item.getName() + "의 현재 수량은 " + item.getStockQuantity() + "개 입니다.");
                    }

                    return OrderItem.createOrderItem(item, item.getName(), item.getPrice(), orderItemDto.getCount());
                })
                .collect(Collectors.toList());

        // Order 생성
        Order order = Order.createOrder(member, orderItems);
        orderRepository.save(order);
    }

    /** 사용자의 모든 주문 조회 (Paging 적용) */
    @Transactional(readOnly = true)
    public OrderPageResponseDto getOrdersByMember(Long memberId, int page, int size) {
        // 회원 정보 조회
        Member member = entityValidationService.validateMember(memberId);

        // 전체 주문의 총합 계산
        int totalOrderPrice = orderRepository.findOrdersByMember(member)
                .stream()
                .mapToInt(Order::getTotalPrice)
                .sum();

        // 페이징 설정
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> ordersPage = orderRepository.searchOrdersByMember(memberId, pageable);

        // 주문 리스트를 OrderResponseDto 변환
        List<OrderResponseDto> orderResponseDtoList = ordersPage.stream()
                .map(order -> {
                    List<OrderItemResponseDto> itemResponseDtoList = order.getOrderItems().stream()
                            .map(orderItem -> OrderItemResponseDto.createOrderItemResponseDto(
                                    orderItem.getId(),
                                    orderItem.getName(),
                                    orderItem.getCount(),
                                    orderItem.getTotalPrice() / orderItem.getCount(),
                                    orderItem.getTotalPrice(),
                                    orderItem.getRepImgUrl())
                            )
                            .collect(Collectors.toList());

                    return OrderResponseDto.createOrderResponseDto(itemResponseDtoList, order.getCreatedAt());
                })
                .collect(Collectors.toList());

        return OrderPageResponseDto.createOrderPageResponseDto(orderResponseDtoList, totalOrderPrice,
                ordersPage.getPageable(), ordersPage.isLast(), ordersPage.getTotalPages(),
                ordersPage.getTotalElements(), ordersPage.getSize(),
                ordersPage.getNumber(), ordersPage.isFirst(),
                ordersPage.getNumberOfElements(), ordersPage.isEmpty());
    }
}
