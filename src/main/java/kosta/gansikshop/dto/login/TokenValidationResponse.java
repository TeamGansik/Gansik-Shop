package kosta.gansikshop.dto.login;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenValidationResponse {
    private final boolean valid;
}
