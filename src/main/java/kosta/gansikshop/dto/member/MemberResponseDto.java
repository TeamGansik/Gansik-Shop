package kosta.gansikshop.dto.member;

import lombok.*;

/**
 * Member 조회용 DTO
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponseDto {
    private String email;
    private String name;
    private String phone;
    private String postcode;
    private String roadAddress;
    private String detailAddress;

    public static MemberResponseDto createMemberResponseDto(String email, String name,
                                                            String phone, String postcode,
                                                            String roadAddress, String detailAddress) {
        return MemberResponseDto.builder()
                .email(email)
                .name(name)
                .phone(phone)
                .postcode(postcode)
                .roadAddress(roadAddress)
                .detailAddress(detailAddress)
                .build();
    }
}
