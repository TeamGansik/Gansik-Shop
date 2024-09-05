package kosta.gansikshop.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Address {
    private String postcode;
    private String roadAddress;
    private String detailAddress;

    @Builder
    private Address(String postcode, String roadAddress, String detailAddress) {
        this.postcode = postcode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }

    public static Address createAddress(String postcode, String roadAddress, String detailAddress) {
        return Address.builder().postcode(postcode).roadAddress(roadAddress).detailAddress(detailAddress).build();
    }
}
