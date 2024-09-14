package kosta.gansikshop.repository.item;

import kosta.gansikshop.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item,Long>, ItemRepositoryCustom {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}