package com.example.outsourcing.domain.owner.review.repository;

import com.example.outsourcing.domain.owner.review.entity.OwnerReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OwnerReviewRepository extends JpaRepository<OwnerReview, Long> {

    @Query("select o from OwnerReview o join fetch o.review r where r.id = :reviewId and r.deletedAt is null")
    Optional<OwnerReview> findByReviewId(@Param("reviewId") Long reviewId);

    @Query("select o from OwnerReview o where o.review.shop.id = :shopId and o.deletedAt is null")
    @EntityGraph(attributePaths = {"user", "review", "review.shop"})
    Page<OwnerReview> findAllOwnerReviewsByShopId(@Param("shopId") Long shopId, Pageable pageable);
}
