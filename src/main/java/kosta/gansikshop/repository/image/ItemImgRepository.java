package kosta.gansikshop.repository.image;

import kosta.gansikshop.domain.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long>, ItemImgRepositoryCustom {

}
