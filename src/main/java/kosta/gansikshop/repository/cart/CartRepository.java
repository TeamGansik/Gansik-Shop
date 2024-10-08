package kosta.gansikshop.repository.cart;

import kosta.gansikshop.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {

    void deleteAllByMemberIdAndItemIdIn(Long memberId, List<Long> itemIds);

    // CartId 리스트에 해당하는 장바구니 항목을 삭제
    void deleteAllByMemberIdAndIdIn(Long memberId, List<Long> cartItemIds);
}