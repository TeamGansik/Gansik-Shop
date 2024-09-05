package kosta.gansikshop.dto.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartRequestDto {
    private Long memberId;
    private Long itemId;
    private int count;

    @Builder
    public CartRequestDto(Long memberId, Long itemId, int count) {
        this.memberId = memberId;
        this.itemId = itemId;
        this.count = count;
    }
}
