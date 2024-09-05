package kosta.gansikshop.service;

import kosta.gansikshop.domain.Cart;
import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.Member;
import kosta.gansikshop.dto.cart.CartItemDto;
import kosta.gansikshop.dto.cart.CartRequestDto;
import kosta.gansikshop.dto.cart.CartResponseDto;
import kosta.gansikshop.repository.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final EntityValidationService entityValidationService;

    @Transactional
    public boolean addCart(Long memberId, CartRequestDto cartRequestDto) {
        Member member = entityValidationService.validateMember(memberId);
        Item item = entityValidationService.validateItem(cartRequestDto.getItemId());

        List<Cart> carts = cartRepository.findCartDetails(member.getId(), Optional.of(item.getId()));
        Cart existingCart = carts.isEmpty() ? null : carts.getFirst();

        if (existingCart != null) {
            existingCart.updateCount(existingCart.getCount() + cartRequestDto.getCount());
            return false;
        } else {
            Cart newCart = Cart.createCart(member, item, cartRequestDto.getCount());
            cartRepository.save(newCart);
            return true;
        }
    }

    @Transactional
    public void deleteCartItems(Long memberId, List<Long> cartItemIds) {
        if (cartItemIds.isEmpty()) {
            throw new IllegalArgumentException("아무 상품도 선택되지 않았습니다.");
        }
        cartRepository.deleteAllByMemberIdAndItemIdIn(memberId, cartItemIds);
    }

    @Transactional(readOnly = true)
    public CartResponseDto getCartItems(Long memberId) {
        List<Cart> carts = cartRepository.findCartDetails(memberId, Optional.empty());
        List<CartItemDto> cartItemDtoList = carts.stream()
                .map(cart -> CartItemDto.createCartItemDto(cart.getItem(), cart.getCount()))
                .collect(Collectors.toList());

        return CartResponseDto.createCartResponseDto(cartItemDtoList);
    }
}
