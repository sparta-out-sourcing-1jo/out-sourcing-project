package com.example.outsourcing.domain.review.service;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.review.dto.request.CreateReviewRequestDto;
import com.example.outsourcing.domain.review.dto.request.UpdateReviewRequestDto;
import com.example.outsourcing.domain.review.dto.response.CreateReviewResponseDto;
import com.example.outsourcing.domain.review.dto.response.GetReviewResponseDto;
import com.example.outsourcing.domain.review.dto.response.UpdateReviewResponseDto;
import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Pageable;

import static com.example.outsourcing.common.enums.OrderState.FINISH;
import static com.example.outsourcing.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    // 리뷰 생성 서비스 로직
    @Transactional
    public CreateReviewResponseDto createReview(
            CreateReviewRequestDto dto,
            MultipartFile file,
            Long orderId
    ) {
        // 리뷰 어뷰징 방지 (1주문 당 1리뷰)
        if(reviewRepository.findByOrderId(orderId).isPresent()) {
            throw new ResponseStatusException(
                    REVIEW_ALREADY_EXIST.getStatus(),
                    REVIEW_ALREADY_EXIST.getMessage()
            );
        }

        Order findOrder = getOrder(orderId);

        if(findOrder.getState() != FINISH) {
            throw new ResponseStatusException(
                    ORDER_NOT_FINISH.getStatus(),
                    ORDER_NOT_FINISH.getMessage()
            );
        }

        String imageUrl = null;
        if(file != null && !file.isEmpty()) {
            imageUrl = s3Uploader.uploadFile(file);
        }

        Review savedReview = Review.builder()
                .content(dto.getContent())
                .rating(dto.getRating())
                .imageUrl(imageUrl)
                .user(findOrder.getUser())
                .shop(findOrder.getShop())
                .order(findOrder)
                .build();

        reviewRepository.save(savedReview);
        return CreateReviewResponseDto.of(savedReview);
    }

    // 리뷰 전체 조회 서비스 로직
    @Transactional(readOnly = true)
    public PageResponseDto<GetReviewResponseDto> findReviews(Long shopId, Pageable pageable) {
        Shop findShop = getShop(shopId);
        Page<Review> reviews = reviewRepository.findAllReviewsByShopId(findShop.getId(), pageable);
        return new PageResponseDto<>(reviews.map(GetReviewResponseDto::of));
    }

    // 리뷰 단건 수정 서비스 로직
    @Transactional
    public UpdateReviewResponseDto updateReview(
            UpdateReviewRequestDto dto,
            MultipartFile file,
            Long reviewId,
            Long userId
    ) {
        checkUserPermission(reviewId, userId);
        Review findReview = getReview(reviewId);

        String imageUrl = findReview.getImageUrl();
        if(file != null && !file.isEmpty()) {
            imageUrl = s3Uploader.uploadFile(file);
        }

        findReview.reviewUpdate(dto.getContent(), dto.getRating(), imageUrl);
        return UpdateReviewResponseDto.of(findReview);
    }
    
    // 리뷰 단건 삭제 서비스 로직
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        checkUserPermission(reviewId, userId);

        getReview(reviewId).setDeletedAt();
    }

    // 주문을 가져오는 메서드 주문이 없을 시 NOT_FOUND
    protected Order getOrder(Long orderId) {
        return orderRepository.findOrderById(orderId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                ORDER_NOT_FOUND.getStatus(),
                                ORDER_NOT_FOUND.getMessage()
                        )
                );
    }

    // 가게를 가져오는 메서드 가게가 없을 시 NOT_FOUND
    protected Shop getShop(Long shopId) {
        return shopRepository.findShopById(shopId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                SHOP_NOT_FOUND.getStatus(),
                                SHOP_NOT_FOUND.getMessage()
                        )
                );
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

    // 유저를 가져오는 메서드 유저가 없을 시 NOT_FOUND
    protected User getUser(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                USER_NOT_FOUND.getStatus(),
                                USER_NOT_FOUND.getMessage()
                        )
                );
    }
    
    // 해당 유저가 해당 리뷰를 작성한 유저인지 확인하는 메서드
    protected void checkUserPermission(Long reviewId, Long userId) {
        if(!ObjectUtils.nullSafeEquals(userId, getReview(reviewId).getUser().getId())) {
            throw new ResponseStatusException(
                    USER_ACCESS_DENIED.getStatus(),
                    USER_ACCESS_DENIED.getMessage()
            );
        }
    }
}