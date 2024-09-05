package kosta.gansikshop.repository.order;

import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.Member;
import kosta.gansikshop.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    /** 성능 최적화 X */
    List<Order> findByMember(Member member);

    /** 성능 최적화 O (한방 쿼리) Paging X */
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.item WHERE o.member = :member")
    List<Order> findOrdersWithItemsByMember(@Param("member") Member member);

    @Query("SELECT o FROM Order o JOIN FETCH o.member m WHERE m.id = :memberId")
    Page<Order> findOrdersWithMemberByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    Page<Order> findByMember(Member member, Pageable pageable);

    boolean existsByOrderItemsItem(Item item);
}
