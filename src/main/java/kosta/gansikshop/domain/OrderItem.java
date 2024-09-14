package kosta.gansikshop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String name;
    private int orderPrice;
    private int count;

    @Builder
    private OrderItem(Order order, Item item, String name, int orderPrice, int count) {
        this.order = order;
        this.item = item;
        this.name = item.getName();
        this.orderPrice = orderPrice; //주문 가격
        this.count = count; // 주문 수량
    }

    /** 대표 이미지 가져오기 */
    public String getRepImgUrl() {
        return item.getImages().stream()
                .filter(ItemImg::isRepImgYn)
                .map(ItemImg::getImgUrl)
                .findFirst()
                .orElse(null);
    }


    /** 연관 관계 메서드 */
    protected void setOrder(Order order) {
        this.order = order;
        if (!order.getOrderItems().contains(this)) {
            order.getOrderItems().add(this);
        }
    }

    /** OrderItem 생성 메서드*/
    public static OrderItem createOrderItem(Item item, String name, int orderPrice, int count) {
        item.removeStock(count);
        return OrderItem.builder().item(item).name(name).orderPrice(orderPrice).count(count).build();
    }

    /** Business Logic */
    /** 주문 취소 */
    public void cancel() {
        getItem().addStock(count);
    }

    /** Check Logic*/
    /** 주문 상품 전체 가격 조회*/
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
