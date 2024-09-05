package kosta.gansikshop.dto.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemRequestDto {
    private String name;
    private int price;
    private int stockQuantity;
    private String category;

    @Builder
    public ItemRequestDto(String name, int price, int stockQuantity, String category) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }
}
