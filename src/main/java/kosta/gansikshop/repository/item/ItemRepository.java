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

    @EntityGraph(attributePaths = "images")
    Page<Item> findAll(Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.name LIKE %:keyword%")
    Page<Item> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);

    // 전체 검색을 위한 메서드 (키워드가 없는 경우)
    @Query("SELECT i FROM Item i")
    Page<Item> findAllItems(Pageable pageable);

    Page<Item> findByCategory(String category, Pageable pageable);
}