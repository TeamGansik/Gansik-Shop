package kosta.gansikshop.controller;

import kosta.gansikshop.aop.SecurityAspect;
import kosta.gansikshop.dto.order.OrderPageResponseDto;
import kosta.gansikshop.dto.order.OrderRequestDto;
import kosta.gansikshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /** 즉시 구매 */
    @PostMapping("/instant")
    public ResponseEntity<?> saveOrder(@RequestBody OrderRequestDto requestDto) {
        Long memberId = SecurityAspect.getCurrentMemberId();
        orderService.saveOrder(memberId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("상품을 성공적으로 주문했습니다.");
    }

    /** 장바구니 구매 */
    @PostMapping("/cart")
    public ResponseEntity<String> processOrder(@RequestBody OrderRequestDto requestDto) {
        Long memberId = SecurityAspect.getCurrentMemberId();
        orderService.processOrder(memberId, requestDto.getOrderItems());
        return ResponseEntity.status(HttpStatus.CREATED).body("상품을 성공적으로 주문했습니다.");
    }

    /** 사용자의 모든 주문 조회 (페이징) */
    @GetMapping
    public ResponseEntity<?> getOrdersByMember(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Long memberId = SecurityAspect.getCurrentMemberId();
        OrderPageResponseDto responseDto = orderService.getOrdersByMember(memberId, page, size);
        return ResponseEntity.status(responseDto.getContent().isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK).body(responseDto);
    }
}
