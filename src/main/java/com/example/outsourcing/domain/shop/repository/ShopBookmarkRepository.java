package com.example.outsourcing.domain.shop.repository;

import com.example.outsourcing.domain.shop.entity.ShopBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopBookmarkRepository extends JpaRepository<ShopBookmark, Long> {

    Optional<ShopBookmark> findByShopIdAndUserId(Long shopId, Long userId);

    Page<ShopBookmark> findByUserId(Long userId, Pageable pageable);
}
