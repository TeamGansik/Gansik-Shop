package kosta.gansikshop.dto.member;

import lombok.Builder;
import lombok.Getter;

/**
 * 회원 수정용 DTO
 */
@Getter
@Builder
public class MemberUpdateFormDto {
    private String name;
    private String password;
    private String phone;
    private String postcode;
    private String roadAddress;
    private String detailAddress;
}
