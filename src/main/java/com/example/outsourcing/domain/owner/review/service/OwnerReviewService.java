package com.example.outsourcing.domain.owner.review.service;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.owner.review.dto.request.CreateOwnerReviewRequestDto;
import com.example.outsourcing.domain.owner.review.dto.response.CreateOwnerReviewResponseDto;
import com.example.outsourcing.domain.owner.review.dto.response.GetOwnerReviewResponseDto;
import com.example.outsourcing.domain.owner.review.entity.OwnerReview;
import com.example.outsourcing.domain.owner.review.repository.OwnerReviewRepository;
import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.outsourcing.common.enums.UserRole.OWNER;
import static com.example.outsourcing.common.exception.ErrorCode.*;
import static com.example.outsourcing.common.exception.ErrorCode.REVIEW_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OwnerReviewService {

    private final ReviewRepository reviewRepository;
    private final OwnerReviewRepository ownerReviewRepository;
    private final ShopRepository shopRepository;

    // 사장 리뷰 생성 서비스 로직
    @Transactional
    public CreateOwnerReviewResponseDto createOwnerReview(
            CreateOwnerReviewRequestDto dto,
            Long reviewId,
            AuthUser authUser
    ) {
        // 사장 리뷰 중복 확인
        if(ownerReviewRepository.findByReviewId(reviewId).isPresent()) {
            throw new ResponseStatusException(
                    REVIEW_ALREADY_EXIST.getStatus(),
                    REVIEW_ALREADY_EXIST.getMessage()
            );
        }

        // 해당 유저의 역할이 사장인지 확인
        if(!authUser.getUserRole().equals(OWNER)) {
            throw new ResponseStatusException(
                    USER_ACCESS_DENIED.getStatus(),
                    USER_ACCESS_DENIED.getMessage()
            );
        }

        // 해당 리뷰가 사장의 가게에 속하는지 확인
        if(!getReview(reviewId).getShop().getUser().getId().equals(authUser.getId())) {
            throw new ResponseStatusException(
                    USER_ACCESS_DENIED.getStatus(),
                    USER_ACCESS_DENIED.getMessage()
            );
        }

        OwnerReview savedReview = OwnerReview.builder()
                .content(dto.getContent())
                .user(getReview(reviewId).getShop().getUser())
                .review(getReview(reviewId))
                .build();

        ownerReviewRepository.save(savedReview);

        return CreateOwnerReviewResponseDto.of(savedReview);
    }

    // 사장 리뷰 조회 서비스 로직
    @Transactional(readOnly = true)
    public PageResponseDto<GetOwnerReviewResponseDto> findOwnerReviews(
            Long shopId,
            Pageable pageable
    ) {
        Shop findShop = getShop(shopId);
        Page<OwnerReview> reviews = ownerReviewRepository.findAllOwnerReviewsByShopId(findShop.getId(), pageable);
        return new PageResponseDto<>(reviews.map(GetOwnerReviewResponseDto::of));
    }

    // 리뷰를 가져오는 메서드 리뷰가 없을 시 NOT_FOUND
    protected Review getReview(Long reviewId) {
        return reviewRepository.findReviewById(reviewId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                REVIEW_NOT_FOUND.getStatus(),
                                REVIEW_NOT_FOUND.getMessage()
                        )
                );
    }

    // 가게를 가져온느 메서드 가게 없을 시 NOT_FOUND
    protected Shop getShop(Long shopId) {
        return shopRepository.findShopById(shopId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                SHOP_NOT_FOUND.getStatus(),
                                SHOP_NOT_FOUND.getMessage()
                        )
                );
    }
}
