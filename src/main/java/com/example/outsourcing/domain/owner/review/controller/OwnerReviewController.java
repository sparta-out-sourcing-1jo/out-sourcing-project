package com.example.outsourcing.domain.owner.review.controller;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.domain.auth.annotation.Auth;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.owner.review.dto.request.CreateOwnerReviewRequestDto;
import com.example.outsourcing.domain.owner.review.dto.response.CreateOwnerReviewResponseDto;
import com.example.outsourcing.domain.owner.review.dto.response.GetOwnerReviewResponseDto;
import com.example.outsourcing.domain.owner.review.service.OwnerReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
public class OwnerReviewController {

    private final OwnerReviewService ownerReviewService;

    @PostMapping("/reviews/{reviewId}/ownerReviews")
    public ResponseEntity<CreateOwnerReviewResponseDto> createOwnerReview(
            @Valid @RequestBody CreateOwnerReviewRequestDto dto,
            @PathVariable Long reviewId,
            @Auth AuthUser authUser
    ) {
        return new ResponseEntity<>(
                ownerReviewService.createOwnerReview(dto, reviewId, authUser),
                HttpStatus.OK
        );
    }

    @GetMapping("/ownerReviews")
    public ResponseEntity<PageResponseDto<GetOwnerReviewResponseDto>> findOwnerReviews(
            @RequestParam Long shopId,
            @PageableDefault(page = 1, size = 10, sort = "updatedAt", direction = DESC) Pageable pageable
    ) {
        Pageable convertPageable = PageRequest.of(
                pageable.getPageNumber()-1,
                pageable.getPageSize(),
                pageable.getSort()
        );

        PageResponseDto<GetOwnerReviewResponseDto> getOwnerReviews
                = ownerReviewService.findOwnerReviews(shopId, convertPageable);

        return new ResponseEntity<>(
                getOwnerReviews,
                HttpStatus.OK
        );
    }
}
