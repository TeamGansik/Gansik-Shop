package kosta.gansikshop.repository.image;

import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.ItemImg;

import java.util.List;

public interface ItemImgRepositoryCustom {
    List<ItemImg> findByItemWithImages(Item item);
}
