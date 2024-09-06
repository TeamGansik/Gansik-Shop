package kosta.gansikshop.dto.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemResponseDto {
    private Long itemId;
    private String itemName;
    private int quantity;
    private int itemPrice;
    private int totalPrice; // 개별 아이템의 총 가격
    private String itemImageUrl; // 아이템의 대표 이미지 URL

    @Builder
    private OrderItemResponseDto(Long itemId, String itemName, int quantity, int itemPrice, int totalPrice, String itemImageUrl) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
        this.totalPrice = totalPrice;
        this.itemImageUrl = itemImageUrl;
    }

    public static OrderItemResponseDto createOrderItemResponseDto(Long itemId, String itemName, int quantity,
                                                                  int itemPrice, int totalPrice, String itemImageUrl) {

        return OrderItemResponseDto.builder()
                .itemId(itemId)
                .itemName(itemName)
                .quantity(quantity)
                .itemPrice(itemPrice)
                .totalPrice(totalPrice)
                .itemImageUrl(itemImageUrl)
                .build();
    }
}
