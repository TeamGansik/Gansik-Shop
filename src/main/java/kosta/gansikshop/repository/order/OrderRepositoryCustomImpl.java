package kosta.gansikshop.repository.order;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kosta.gansikshop.domain.Order;
import kosta.gansikshop.domain.QMember;
import kosta.gansikshop.domain.QOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> searchOrdersByMember(Long memberId, Pageable pageable) {
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        // 동적 쿼리 조건 추가 (memberId는 필수 조건)
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(order.member.id.eq(memberId));

        // Content 쿼리 (데이터 페칭)
        List<Order> content = queryFactory.selectFrom(order)
                .join(order.member, member).fetchJoin()   // member Fetch Join
                .where(builder)
                .offset(pageable.getOffset())              // 페이징 시작점
                .limit(pageable.getPageSize())             // 페이지 크기
                .fetch();

        // Count 쿼리 (전체 레코드 수 페칭) - 조건부로 실행되도록 정의
        JPAQuery<Long> countQuery = queryFactory.select(order.count())
                .from(order)
                .where(builder);

        // PageableExecutionUtils 사용해 필요할 때만 count 쿼리 실행
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

}
