package com.example.outsourcing.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateReviewRequestDto {

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    @NotEmpty(message = "별점을 입력해주세요")
    @Min(value = 1, message = "별점은 1점 이상 입니다")
    @Max(value = 5, message = "별점은 5점 이하 입니다")
    private Integer rating;
}
