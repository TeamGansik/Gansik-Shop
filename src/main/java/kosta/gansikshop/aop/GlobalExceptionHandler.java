package kosta.gansikshop.aop;

import kosta.gansikshop.exception.NotEnoughStockException;
import kosta.gansikshop.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 Bad Request 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // 403 Forbidden 처리
    @ExceptionHandler(AccessDeniedException.class) // 추가
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근이 거부되었습니다: " + e.getMessage());
    }

    // 404 Not Found 처리
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // 재고 부족 예외 처리
    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<String> handleNotEnoughStockException(NotEnoughStockException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("재고가 부족합니다: " + e.getMessage());
    }

    // 500 Internal Server Error 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
    }
}
