package kosta.gansikshop.dto.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponseDto {
    private List<OrderItemResponseDto> orderItems; // 각 주문 항목 정보
    private LocalDateTime createTime;

    @Builder
    private OrderResponseDto(List<OrderItemResponseDto> orderItems, LocalDateTime createTime) {
        this.orderItems = orderItems;
        this.createTime = createTime;
    }

    public static OrderResponseDto createOrderResponseDto(List<OrderItemResponseDto> orderItems, LocalDateTime createTime) {
        return OrderResponseDto.builder().orderItems(orderItems).createTime(createTime).build();
    }
}
