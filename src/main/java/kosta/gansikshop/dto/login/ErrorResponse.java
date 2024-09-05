package kosta.gansikshop.dto.login;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private String message;

    @Builder
    private ErrorResponse(String message) {
        this.message = message;
    }

    public static ErrorResponse createErrorResponse(String message) {
        return ErrorResponse.builder().message(message).build();
    }
}
