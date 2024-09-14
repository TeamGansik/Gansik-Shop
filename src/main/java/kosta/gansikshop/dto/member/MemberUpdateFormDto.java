package kosta.gansikshop.dto.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 수정용 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberUpdateFormDto {
    private String name;
    private String password;
    private String phone;
    private String postcode;
    private String roadAddress;
    private String detailAddress;

    @Builder
    private MemberUpdateFormDto(String name, String password, String phone, String postcode, String roadAddress, String detailAddress) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.postcode = postcode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }
}
