package kosta.gansikshop.repository.image;

import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long>, ItemImgRepositoryCustom {
    List<ItemImg> findByItem(Item item);
    Optional<ItemImg> findByItemAndRepImgYn(Item item, String repImgYn);

    @Query("SELECT i FROM ItemImg i JOIN FETCH i.item WHERE i.item = :item")
    List<ItemImg> findByItemWithFetchJoin(@Param("item") Item item);
}
