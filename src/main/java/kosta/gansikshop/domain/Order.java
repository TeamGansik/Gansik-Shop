package kosta.gansikshop.domain;

import jakarta.persistence.*;
import kosta.gansikshop.domain.baseentity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    private Order(Member member, List<OrderItem> orderItems) {
        setMember(member);
        for (OrderItem orderItem : orderItems) {
            addOrderItem(orderItem);
        }
    }

    /** Order 생성 메서드 */
    public static Order createOrder(Member member, List<OrderItem> orderItems) {
        return Order.builder()
                .member(member)
                .orderItems(orderItems)
                .build();
    }

    /** 연관 관계 메서드*/
    protected void setMember(Member member) {
        this.member = member;
//        member.getOrders().add(this); // 양방향 관계 설정 시 주석 해제
    }

    protected void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /** Business Logic **/

    /** 주문 취소 */
    public void cancel() {
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    /** 전체 주문 가격 조회 */
    public int getTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}

