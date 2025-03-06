package com.example.outsourcing.domain.review.controller;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.domain.auth.annotation.Auth;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.review.dto.request.CreateReviewRequestDto;
import com.example.outsourcing.domain.review.dto.request.UpdateReviewRequestDto;
import com.example.outsourcing.domain.review.dto.response.CreateReviewResponseDto;
import com.example.outsourcing.domain.review.dto.response.GetReviewResponseDto;
import com.example.outsourcing.domain.review.dto.response.UpdateReviewResponseDto;
import com.example.outsourcing.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 생성
    @PostMapping(value = "/orders/{orderId}/reviews", consumes = {"multipart/form-data"})
    public ResponseEntity<CreateReviewResponseDto> createReview(
            @RequestPart(value = "dto") @Valid CreateReviewRequestDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @PathVariable Long orderId
    ) {
        return new ResponseEntity<>(
                reviewService.createReview(dto, file, orderId),
                HttpStatus.OK
        );
    }

    // 리뷰 전체 조회
    @GetMapping("/reviews")
    public ResponseEntity<PageResponseDto<GetReviewResponseDto>> findReviews(
            @RequestParam Long shopId,
            @PageableDefault(page = 1, size = 10, sort = "updatedAt", direction = DESC) Pageable pageable
    ) {
        Pageable convertPageable = PageRequest.of(
                pageable.getPageNumber()-1,
                pageable.getPageSize(),
                pageable.getSort()
        );

        PageResponseDto<GetReviewResponseDto> getReviews =
                reviewService.findReviews(shopId, convertPageable);

        return new ResponseEntity<>(
                getReviews,
                HttpStatus.OK
        );
    }

    // 리뷰 단건 수정
    @PutMapping(value = "/reviews", consumes = {"multipart/form-data"})
    public ResponseEntity<UpdateReviewResponseDto> updateReview(
            @RequestPart(value = "dto") @Valid UpdateReviewRequestDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam Long reviewId,
            @Auth AuthUser authUser
    ) {
        return new ResponseEntity<>(
                reviewService.updateReview(dto, file, reviewId, authUser.getId()),
                HttpStatus.OK
        );
    }

    // 리뷰 단건 삭제
    @DeleteMapping("/reviews")
    public ResponseEntity<Void> deleteReview(
            @RequestParam Long reviewId,
            @Auth AuthUser authUser
    ) {
        reviewService.deleteReview(reviewId, authUser.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
