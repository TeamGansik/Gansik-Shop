package kosta.gansikshop.repository.item;

import kosta.gansikshop.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item,Long>, ItemRepositoryCustom {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}