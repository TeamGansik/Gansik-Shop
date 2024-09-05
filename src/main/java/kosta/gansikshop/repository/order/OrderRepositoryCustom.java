package kosta.gansikshop.repository.order;

import kosta.gansikshop.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    Page<Order> searchOrdersByMember(Long memberId, Pageable pageable);
}
