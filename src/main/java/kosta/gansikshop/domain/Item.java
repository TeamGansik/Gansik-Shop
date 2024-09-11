package kosta.gansikshop.domain;

import jakarta.persistence.*;
import kosta.gansikshop.domain.baseentity.BaseEntity;
import kosta.gansikshop.exception.NotEnoughStockException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String category;

    @OneToMany(mappedBy = "item", cascade = ALL, orphanRemoval = true)
    private List<ItemImg> images = new ArrayList<>();

    @Builder
    private Item(String name, int price, int stockQuantity, String category) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public static Item createItem(String name, int price, int stockQuantity, String category) {
        return Item.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .category(category)
                .build();
    }

    public void update(String name, int price, int stockQuantity, String category) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public void addImage(ItemImg itemImg) {
        images.add(itemImg);
        itemImg.setItem(this);
    }

    public void removeImage(ItemImg itemImg) {
        images.remove(itemImg);
        itemImg.setItem(null);
    }


    /** Business Logic */

    /** 재고 증가 */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /** 재고 감소 */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("Not Enough Stock");
        }
        this.stockQuantity = restStock;
    }

    public void updateModified() {
        this.modifiedAt = LocalDateTime.now();  // 수정 시간 갱신
        // 수정자 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String currentUserName = authentication.getName();  // 현재 인증된 사용자명 (이메일)
            this.setUpdatedBy(currentUserName);  // 수정자 갱신
        }
    }
}
