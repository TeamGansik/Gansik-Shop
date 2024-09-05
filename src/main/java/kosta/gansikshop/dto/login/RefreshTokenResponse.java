package kosta.gansikshop.dto.login;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshTokenResponse {
    private String newToken;

    @Builder
    private RefreshTokenResponse(String newToken) {
        this.newToken = newToken;
    }

    public static RefreshTokenResponse createRefreshTokenResponse(String newToken) {
        return RefreshTokenResponse.builder().newToken(newToken).build();
    }
}
