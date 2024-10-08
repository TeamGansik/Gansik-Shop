package kosta.gansikshop.dto.member;

import lombok.*;

/**
 * Member 조회용 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponseDto {
    private String email;
    private String name;
    private String phone;
    private String postcode;
    private String roadAddress;
    private String detailAddress;
    private String role;

    @Builder
    private MemberResponseDto(String email, String name, String phone, String postcode, String roadAddress, String detailAddress, String role) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.postcode = postcode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.role = role;
    }

    public static MemberResponseDto createMemberResponseDto(String email, String name,
                                                            String phone, String postcode,
                                                            String roadAddress, String detailAddress, String role) {
        return MemberResponseDto.builder()
                .email(email)
                .name(name)
                .phone(phone)
                .postcode(postcode)
                .roadAddress(roadAddress)
                .detailAddress(detailAddress)
                .role(role)
                .build();
    }
}
