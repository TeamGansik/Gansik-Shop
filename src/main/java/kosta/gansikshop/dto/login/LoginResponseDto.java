package kosta.gansikshop.dto.login;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;

    @Builder
    private LoginResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static LoginResponseDto createLoginResponseDto(String accessToken, String refreshToken) {
        return LoginResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }
}
