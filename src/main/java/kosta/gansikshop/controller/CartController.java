package kosta.gansikshop.controller;

import kosta.gansikshop.config.security.CustomUserDetails;
import kosta.gansikshop.dto.cart.CartRequestDto;
import kosta.gansikshop.dto.cart.CartResponseDto;
import kosta.gansikshop.service.CartService;
import kosta.gansikshop.service.EntityValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
@Slf4j
public class CartController {

    private final CartService cartService;
    private final EntityValidationService entityValidationService;

    /** 장바구니 추가 */
    @PostMapping
    public ResponseEntity<?> addCart(@RequestBody CartRequestDto cartRequestDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId(); // 이메일로 회원 ID 조회

            boolean isNewCart = cartService.addCart(memberId, cartRequestDto);
            if (isNewCart) {
                return ResponseEntity.status(HttpStatus.CREATED).body("장바구니에 상품이 추가되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.OK).body("장바구니에 담긴 상품의 수량이 추가되었습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }

    /** 장바구니 삭제 */
    @DeleteMapping
    public ResponseEntity<String> deleteSelectedCarts(@RequestParam List<Long> cartItemIds) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId();

            cartService.deleteCartItems(memberId, cartItemIds);
            return ResponseEntity.status(HttpStatus.OK).body("선택한 상품들이 장바구니에서 제거되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }

    /** 장바구니 조회 */
    @GetMapping
    public ResponseEntity<?> getCartItems() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId();

            CartResponseDto cartItems = cartService.getCartItems(memberId);
            return ResponseEntity.status(HttpStatus.OK).body(cartItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }
}
