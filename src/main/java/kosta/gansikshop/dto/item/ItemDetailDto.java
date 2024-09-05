package kosta.gansikshop.dto.item;

import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.ItemImg;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ItemDetailDto {
    private String name;
    private int price;
    private String category;
    private String repImgUrl;  // 대표 이미지 URL
    private List<String> imgUrls;  // 모든 이미지 URL
    private LocalDateTime createdAt;  // 상품 생성 시간 (ISO 8601 형식)
    private LocalDateTime modifiedAt;  // 최종 업데이트 시간 (ISO 8601 형식)

    @Builder
    private ItemDetailDto(String name, int price, String category, String repImgUrl, List<String> imgUrls, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.repImgUrl = repImgUrl;
        this.imgUrls = imgUrls;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    // Item 객체를 ItemDetailDto 변환
    public static ItemDetailDto createDetailDto(Item item) {
        // 대표 이미지 URL
        String repImgUrl = item.getImages().stream()
                .filter(ItemImg::isRepImgYn)
                .map(ItemImg::getImgUrl)
                .findFirst()
                .orElse(null);

        // 모든 이미지 URL (대표 이미지를 첫 번째로 추가하고 나머지 추가)
        List<String> imgUrls = item.getImages().stream()
                .map(ItemImg::getImgUrl)
                .sorted((url1, url2) -> {
                    // 대표 이미지 URL 항상 첫 번째로 배치
                    if (url1.equals(repImgUrl)) return -1;
                    if (url2.equals(repImgUrl)) return 1;
                    return 0;
                })
                .collect(Collectors.toList());

        return ItemDetailDto.builder()
                .name(item.getName())
                .price(item.getPrice())
                .category(item.getCategory())
                .repImgUrl(repImgUrl)
                .imgUrls(imgUrls)
                .createdAt(item.getCreatedAt())
                .modifiedAt(item.getModifiedAt())
                .build();
    }
}
