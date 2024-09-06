package kosta.gansikshop.repository.cart;

import kosta.gansikshop.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {
    void deleteAllByMemberIdAndItemIdIn(Long memberId, List<Long> itemIds);
}