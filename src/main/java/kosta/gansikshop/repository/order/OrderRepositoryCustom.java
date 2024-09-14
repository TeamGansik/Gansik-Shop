package kosta.gansikshop.repository.order;

import kosta.gansikshop.domain.Member;
import kosta.gansikshop.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findOrdersByMember(Member member);
    Page<Order> searchOrdersByMember(Long memberId, Pageable pageable);
}
