package com.example.outsourcing.domain.shop.repository;

import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long>, JpaSpecificationExecutor<Shop> {

    @Query("select s from Shop s where s.id = :shopId and s.deletedAt is null")
    Optional<Shop> findShopById(@Param("shopId") Long shopId);

    // 한 유저 당 폐업안한 가게 개수 카운트
    int countByUserIdAndDeletedAtIsNull(Long userId);

    @EntityGraph(attributePaths = {"orders"})
    List<Shop> findShopsByUserAndDeletedAtIsNull(User user);


    // 가게 다건 조회 페이징 => Specification 으로 전환
//    // 기본
//    Page<Shop> findAllDeletedAtIsNull(Pageable pageable);
//
//    // 카테고리 적용
//    @Query("select s from Shop s where s.deletedAt is null and s.category = :caterory")
//    Page<Shop> findAllDeletedAtIsNullAndCategory(Pageable pageable, @Param("category") ShopCategory category);
//
//    // 검색 적용
//    @Query("select s from Shop s where s.deletedAt is null and s.name like %:name%")
//    Page<Shop> findAllDeletedAtIsNullAndName(Pageable pageable, @Param("name") String name);
//
//    // 둘다 적용
//    @Query("select s from Shop s where s.deletedAt is null and s.category = :caterory and s.name like %:name%")
//    Page<Shop> findAllDeletedAtIsNullAndCategoryAndName(Pageable pageable, @Param("category") ShopCategory category, @Param("name") String name);



}
