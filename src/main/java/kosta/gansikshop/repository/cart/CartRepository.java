package kosta.gansikshop.repository.cart;

import kosta.gansikshop.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {

    Cart findByItemId(Long itemId);

    Cart findCartByMemberId(Long memberId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.member JOIN FETCH c.item WHERE c.member.id = :memberId")
    List<Cart> findByMemberIdWithDetails(@Param("memberId") Long memberId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.member JOIN FETCH c.item WHERE c.member.id = :memberId AND c.item.id = :itemId")
    Cart findByMemberIdAndItemIdWithDetails(@Param("memberId") Long memberId, @Param("itemId") Long itemId);

    void deleteAllByMemberIdAndItemIdIn(Long memberId, List<Long> itemIds);
}