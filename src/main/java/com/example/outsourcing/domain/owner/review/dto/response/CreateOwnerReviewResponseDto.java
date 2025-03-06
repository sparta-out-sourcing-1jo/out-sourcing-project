package com.example.outsourcing.domain.owner.review.dto.response;

import com.example.outsourcing.domain.owner.review.entity.OwnerReview;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateOwnerReviewResponseDto {

    private final Long id;
    private final Long userId;
    private final Long shopId;
    private final Long orderId;
    private final Long reviewId;

    private final String content;
    private final String userRole;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd'T'hh-mm-ss")
    private final LocalDateTime createdAt;

    public static CreateOwnerReviewResponseDto of(OwnerReview ownerReview) {
        return new CreateOwnerReviewResponseDto(
                ownerReview.getId(),
                ownerReview.getUser().getId(),
                ownerReview.getReview().getShop().getId(),
                ownerReview.getReview().getOrder().getId(),
                ownerReview.getReview().getId(),
                ownerReview.getContent(),
                ownerReview.getUser().getRole().name(),
                ownerReview.getCreatedAt()
        );
    }
}
