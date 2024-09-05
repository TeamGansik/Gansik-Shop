package kosta.gansikshop.dto.member;

import lombok.Builder;
import lombok.Getter;

/**
 * 회원 가입용 DTO
 */
@Getter
@Builder
public class MemberSingUpFormDto {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String postcode;
    private String roadAddress;
    private String detailAddress;
}
