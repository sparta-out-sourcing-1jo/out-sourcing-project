package com.example.outsourcing.domain.review.controller;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.util.JwtUtil;
import com.example.outsourcing.domain.review.dto.request.CreateReviewRequestDto;
import com.example.outsourcing.domain.review.dto.request.UpdateReviewRequestDto;
import com.example.outsourcing.domain.review.dto.response.CreateReviewResponseDto;
import com.example.outsourcing.domain.review.dto.response.GetReviewResponseDto;
import com.example.outsourcing.domain.review.service.ReviewService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;

    // 리뷰 생성
    @PostMapping("/orders/{orderId}/reviews")
    public ResponseEntity<CreateReviewResponseDto> createReview(
            @Valid @RequestBody CreateReviewRequestDto dto,
            @PathVariable Long orderId
    ) {
        return new ResponseEntity<>(
                reviewService.createReview(dto, orderId),
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
    @PutMapping("/reviews")
    public ResponseEntity<GetReviewResponseDto> updateReview(
            @Valid @RequestBody UpdateReviewRequestDto dto,
            @RequestParam Long reviewId,
            @RequestHeader("Authorization") String bearerToken
    ) {
        Claims claims = jwtUtil.extractClaims(bearerToken.substring(7));
        Long userId = Long.parseLong(claims.getSubject());

        return new ResponseEntity<>(
                reviewService.updateReview(dto, reviewId, userId),
                HttpStatus.OK
        );
    }

    // 리뷰 단건 삭제
    @DeleteMapping("/reviews")
    public ResponseEntity<Void> deleteReview(
            @RequestParam Long reviewId,
            @RequestHeader("Authorization") String bearerToken
    ) {
        Claims claims = jwtUtil.extractClaims(bearerToken.substring(7));
        Long userId = Long.parseLong(claims.getSubject());
        reviewService.deleteReview(reviewId, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
