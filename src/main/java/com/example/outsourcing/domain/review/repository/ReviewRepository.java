package com.example.outsourcing.domain.review.repository;

import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByOrderId(Long orderId);

    @Query("select r from Review r where r.shop = :shop and r.deletedAt is null")
    Page<Review> findAllReviewsByShop(@Param("shop") Shop shop, Pageable pageable);

    @Query("select r from Review r where r.id = :reviewId and r.deletedAt is null")
    Optional<Review> findReviewById(@Param("reviewId") Long reviewId);

    @Modifying
    @Query("update Review r set r.deletedAt = current_timestamp where r.id = :reviewId")
    void deleteReviewById(@Param("reviewId") Long reviewId);

    @Query(value = "select coalesce(sum(r.rating), 0) from Review r where r.shop.id = :shopId", nativeQuery = true)
    Integer findSumRatingByShopId(@Param("shopId") Long shopId);
}