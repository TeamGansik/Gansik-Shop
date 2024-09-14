package kosta.gansikshop.dto.cart;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartResponseDto {
    private List<CartItemDto> items;

    @Builder
    private CartResponseDto(List<CartItemDto> items) {
        this.items = items;
    }

    public static CartResponseDto createCartResponseDto(List<CartItemDto> items) {
        return CartResponseDto.builder().items(items).build();
    }
}
