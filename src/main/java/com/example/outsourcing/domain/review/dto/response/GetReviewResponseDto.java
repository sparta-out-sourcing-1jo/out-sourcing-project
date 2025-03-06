package com.example.outsourcing.domain.review.dto.response;

import com.example.outsourcing.domain.review.entity.Review;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GetReviewResponseDto {

    private final Long id;
    private final Long userId;
    private final Long shopId;
    private final Long orderId;

    private final String content;
    private final Integer rating;
    private final String userRole;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String imageUrl;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static GetReviewResponseDto of(Review review) {
        return new GetReviewResponseDto(
                review.getId(),
                review.getUser().getId(),
                review.getShop().getId(),
                review.getOrder().getId(),
                review.getContent(),
                review.getRating(),
                review.getUser().getRole().name(),
                review.getImageUrl(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
