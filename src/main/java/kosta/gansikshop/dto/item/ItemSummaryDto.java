package kosta.gansikshop.dto.item;

import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.ItemImg;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ItemSummaryDto {
    private String name;
    private int price;
    private String repImgUrl; // 대표 이미지 URL

    @Builder
    private ItemSummaryDto(String name, int price, String repImgUrl) {
        this.name = name;
        this.price = price;
        this.repImgUrl = repImgUrl;
    }

    public static ItemSummaryDto createSummaryDto(Item item) {
        String repImgUrl = item.getImages().stream()
                .filter(ItemImg::isRepImgYn)
                .map(ItemImg::getImgUrl)
                .findFirst()
                .orElse(null);

        return ItemSummaryDto.builder()
                .name(item.getName())
                .price(item.getPrice())
                .repImgUrl(repImgUrl)
                .build();
    }
}
