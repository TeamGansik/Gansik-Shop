package kosta.gansikshop.dto.image;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImgResponseDto {
    private String imgUrl;
    private boolean isRepImg; // 대표 이미지 여부

    @Builder
    private ItemImgResponseDto(String imgUrl, boolean isRepImg) {
        this.imgUrl = imgUrl;
        this.isRepImg = isRepImg;
    }
}
