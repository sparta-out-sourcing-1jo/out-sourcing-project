package com.example.outsourcing.domain.review.repository;

import com.example.outsourcing.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r join fetch r.order o where o.id = :orderId and o.deletedAt is null")
    Optional<Review> findByOrderId(Long orderId);

    @Query("select r from Review r where r.shop.id = :shopId and r.deletedAt is null")
    @EntityGraph(attributePaths = {"user", "shop", "order"})
    Page<Review> findAllReviewsByShopId(@Param("shopId") Long shopId, Pageable pageable);

    @Query("select r from Review r where r.id = :reviewId and r.deletedAt is null")
    Optional<Review> findReviewById(@Param("reviewId") Long reviewId);

    @Query("select coalesce(sum(r.rating), 0) from Review r where r.shop.id = :shopId and r.deletedAt is null")
    Integer findSumRatingByShopId(@Param("shopId") Long shopId);

    Integer countByShop_IdAndDeletedAtIsNull(Long shopId);
}