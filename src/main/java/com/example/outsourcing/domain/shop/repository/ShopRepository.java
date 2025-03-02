package com.example.outsourcing.domain.shop.repository;

import com.example.outsourcing.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query("select s from Shop s where s.id = :shopId and s.deletedAt is null")
    Optional<Shop> findShopById(@Param("shopId") Long shopId);
}
