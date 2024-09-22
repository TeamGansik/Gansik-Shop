package kosta.gansikshop.controller;

import kosta.gansikshop.aop.SecurityAspect;
import kosta.gansikshop.dto.cart.CartRequestDto;
import kosta.gansikshop.dto.cart.CartResponseDto;
import kosta.gansikshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    /** 장바구니 추가 */
    @PostMapping
    public ResponseEntity<?> addCart(@RequestBody CartRequestDto cartRequestDto) {
        Long memberId = SecurityAspect.getCurrentMemberId();

        boolean isNewCart = cartService.addCart(memberId, cartRequestDto);
        if (isNewCart) {
            return ResponseEntity.status(HttpStatus.CREATED).body("장바구니에 상품이 추가되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("장바구니에 담긴 상품의 수량이 추가되었습니다.");
        }
    }

    /** 장바구니 항목 수량 업데이트 */
    @PutMapping("/{cartItemId}")
    public ResponseEntity<String> updateCartItem(@PathVariable Long cartItemId, @RequestParam int count) {
        Long memberId = SecurityAspect.getCurrentMemberId();

        // 수량이 1 미만으로 내려가지 않도록 처리
        if (count < 1) {
            throw new IllegalArgumentException("최소 수량은 1개입니다.");
        }
        cartService.updateCartItem(memberId, cartItemId, count);
        return ResponseEntity.status(HttpStatus.OK).body("장바구니 항목의 수량이 업데이트되었습니다.");
    }

    /** 장바구니 삭제 */
    @DeleteMapping
    public ResponseEntity<String> deleteSelectedCarts(@RequestParam List<Long> cartItemIds) {
        Long memberId = SecurityAspect.getCurrentMemberId();
        cartService.deleteCartItems(memberId, cartItemIds);
        return ResponseEntity.status(HttpStatus.OK).body("선택한 상품들이 장바구니에서 제거되었습니다.");
    }

    /** 장바구니 조회 */
    @GetMapping
    public ResponseEntity<?> getCartItems() {
        Long memberId = SecurityAspect.getCurrentMemberId();
        CartResponseDto cartItems = cartService.getCartItems(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(cartItems);
    }
}
