package kosta.gansikshop.dto.login;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginDto {
    private String email;
    private String password;

    @Builder
    private LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
