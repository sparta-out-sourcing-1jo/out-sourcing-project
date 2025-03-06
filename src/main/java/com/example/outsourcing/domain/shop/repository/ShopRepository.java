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


}
