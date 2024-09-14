package kosta.gansikshop.dto.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemRequestDto {
    private Long itemId; // 주문할 상품의 ID
    private int count;   // 주문할 상품의 수량

    @Builder
    private OrderItemRequestDto(Long itemId, int count) {
        this.itemId = itemId;
        this.count = count;
    }

    public static OrderItemRequestDto createOrderItemRequestDto(Long itemId, int count) {
        return OrderItemRequestDto.builder().itemId(itemId).count(count).build();
    }
}
