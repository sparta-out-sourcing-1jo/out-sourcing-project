package com.example.outsourcing.domain.review.repository;

import com.example.outsourcing.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Integer countByShop_Id(Long shopId);
}
