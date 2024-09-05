package kosta.gansikshop.domain;

import jakarta.persistence.*;
import kosta.gansikshop.domain.baseentity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;

    @Builder
    private Cart(Member member, Item item, int count) {
        this.member = member;
        this.item = item;
        this.count = count;
    }

    // Cart 생성 메서드
    public static Cart createCart(Member member, Item item, int count) {
        return Cart.builder()
                .member(member)
                .item(item)
                .count(count)
                .build();
    }

    // Cart update
    public void updateCount(int count) {
        this.count = count;
    }
}
