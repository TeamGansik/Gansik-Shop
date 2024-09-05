package kosta.gansikshop.dto.image;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemImgResponseDto {
    private String imgUrl;
    private boolean isRepImg; // 대표 이미지 여부
}
