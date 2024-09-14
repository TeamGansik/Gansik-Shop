package kosta.gansikshop.dto.item;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemRequestDto {
    private String name;
    private int price;
    private int stockQuantity;
    private String category;

    @Builder
    private ItemRequestDto(String name, int price, int stockQuantity, String category) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }
}
