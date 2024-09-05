package kosta.gansikshop.repository.item;

import kosta.gansikshop.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface ItemRepositoryCustom {
    Page<Item> searchItems(String keyword, String category, Pageable pageable);
}
