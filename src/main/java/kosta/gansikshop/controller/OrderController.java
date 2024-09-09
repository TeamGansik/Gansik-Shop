package kosta.gansikshop.controller;

import kosta.gansikshop.config.security.CustomUserDetails;
import kosta.gansikshop.dto.order.OrderPageResponseDto;
import kosta.gansikshop.dto.order.OrderRequestDto;
import kosta.gansikshop.exception.NotEnoughStockException;
import kosta.gansikshop.service.EntityValidationService;
import kosta.gansikshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final EntityValidationService entityValidationService;

    /** 즉시 구매 */
    @PostMapping("/instant")
    public ResponseEntity<?> saveOrder(@RequestBody OrderRequestDto requestDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId(); // 이메일로 회원 ID 조회

            orderService.saveOrder(memberId, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order Success");
        } catch (NotEnoughStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order Failed: " + e.getMessage());
        }
    }


    /** 장바구니 구매 */
    @PostMapping("/cart")
    public ResponseEntity<String> processOrder(@RequestBody OrderRequestDto requestDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId(); // 이메일로 회원 ID 조회

            orderService.processOrder(memberId, requestDto.getOrderItems());
            return ResponseEntity.status(HttpStatus.CREATED).body("Order Success");
        } catch (NotEnoughStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order Failed: " + e.getMessage());
        }
    }

    /** 사용자의 모든 주문 조회 (페이징) */
    @GetMapping
    public ResponseEntity<?> getOrdersByMember(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId(); // 이메일로 회원 ID 조회

            OrderPageResponseDto responseDto = orderService.getOrdersByMember(memberId, page, size);
            return ResponseEntity.status(responseDto.getContent().isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK).body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order retrieval failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving orders.");
        }
    }
}
