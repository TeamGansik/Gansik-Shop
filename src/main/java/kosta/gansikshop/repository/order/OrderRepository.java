package kosta.gansikshop.repository.order;

import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    boolean existsByOrderItemsItem(Item item);
}
