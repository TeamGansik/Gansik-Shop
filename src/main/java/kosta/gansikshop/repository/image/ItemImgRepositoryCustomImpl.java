package kosta.gansikshop.repository.image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.ItemImg;
import kosta.gansikshop.domain.QItemImg;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ItemImgRepositoryCustomImpl implements ItemImgRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ItemImg> findByItemWithImages(Item item) {
        QItemImg itemImg = QItemImg.itemImg;

        return queryFactory.selectFrom(itemImg)
                .where(itemImg.item.eq(item))
                .fetch();
    }
}
