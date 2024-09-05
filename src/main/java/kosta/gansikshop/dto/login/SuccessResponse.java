package kosta.gansikshop.dto.login;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SuccessResponse {
    private String message;

    @Builder
    private SuccessResponse(String message) {
        this.message = message;
    }


}
