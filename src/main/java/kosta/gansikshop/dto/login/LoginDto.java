package kosta.gansikshop.dto.login;

import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Builder
public class LoginDto {
    @NotBlank(message = "회원 가입할 이메일을 입력해 주세요.")
    @Email(message = "이메일 형식으로 입력해 주세요")
    private String email;

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자 이상, 최대 16자 이하로만 설정할 수 있습니다.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%])(?=.*[0-9])[a-zA-Z0-9!@#$%]{8,16}$",
            message = "비밀번호는 영어 대소문자, 숫자, !@#$%로만 이루어지고, 영어 대문자와 특수 문자를 최소 하나 이상 포함해야 합니다.")
    private String password;
}
