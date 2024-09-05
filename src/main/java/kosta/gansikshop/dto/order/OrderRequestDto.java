package kosta.gansikshop.dto.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderRequestDto {
    private List<OrderItemRequestDto> orderItems;

    @Builder
    private OrderRequestDto(List<OrderItemRequestDto> orderItems) {
        this.orderItems = orderItems;
    }

    public static OrderRequestDto createOrderRequestDto(List<OrderItemRequestDto> orderItems) {
        return OrderRequestDto.builder().orderItems(orderItems).build();
    }
}
