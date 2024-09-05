package kosta.gansikshop.repository.item;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.QItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> searchItems(String keyword, String category, Pageable pageable) {
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(item.name.containsIgnoreCase(keyword));
        }

        if (category != null && !category.isEmpty()) {
            builder.and(item.category.eq(category));
        }

        // Content 쿼리 (데이터 페칭)
        List<Item> content = queryFactory.selectFrom(item)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리 (전체 레코드 수 페칭) - 조건부로 실행되도록 정의
        JPAQuery<Long> countQuery = queryFactory.select(item.count())
                .from(item)
                .where(builder);

        // PageableExecutionUtils.getPage 사용하여 카운트 쿼리를 생략할 수 있는 경우 자동으로 처리
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
