package kosta.gansikshop.dto.cart;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartRequestDto {
    private Long memberId;
    private Long itemId;
    private int count;

    @Builder
    private CartRequestDto(Long memberId, Long itemId, int count) {
        this.memberId = memberId;
        this.itemId = itemId;
        this.count = count;
    }
}
