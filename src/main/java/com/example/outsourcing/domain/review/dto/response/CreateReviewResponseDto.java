package com.example.outsourcing.domain.review.dto.response;

import com.example.outsourcing.domain.review.entity.Review;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateReviewResponseDto {

    private final Long id;
    private final Long userId;
    private final Long shopId;
    private final Long orderId;

    private final String content;
    private final Integer rating;
    private final LocalDateTime createdAt;

    public static CreateReviewResponseDto of(Review review) {
        return new CreateReviewResponseDto(
                review.getId(),
                review.getOrder().getUserId(),
                review.getShop().getId(),
                review.getOrder().getId(),
                review.getContent(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}