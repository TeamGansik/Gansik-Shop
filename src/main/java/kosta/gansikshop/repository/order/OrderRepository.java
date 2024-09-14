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
    boolean existsByOrderItemsItem(Item item);
}
