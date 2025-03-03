package com.example.outsourcing.domain.review.service;

import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.review.dto.request.CreateReviewRequestDto;
import com.example.outsourcing.domain.review.dto.response.CreateReviewResponseDto;
import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.review.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void 주문_조회_시_주문이_없다면_NOT_FOUND_에러() {
        // given
        long orderId = 1L;
        given(orderRepository.findOrderById(orderId)).willReturn(Optional.empty());

        // when
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.getOrder(orderId)
        );

        // then
        assertEquals("해당 주문을 찾을 수 없습니다", exception.getMessage());
    }

    @Test
    void 주문이_FINISH_상태가_아닐_시_CONFLICT_에러() {
        // given
        Long orderId = 1L;
        Order order = new Order(
                OrderState.OWNER_ACCEPT,
                1L,
                "석연걸",
                1L,
                "김밥천국",
                "김치찜",
                10000.0
        );
        ReflectionTestUtils.setField(order, "id", orderId);
        CreateReviewRequestDto dto = new CreateReviewRequestDto("리뷰1", 5);

        given(orderRepository.findOrderById(orderId)).willReturn(Optional.of(order));

        // when
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.createReview(dto, orderId)
        );

        // then
        assertEquals("아직 해당 주문이 도착하지 않았습니다", exception.getMessage());
    }

    @Test
    void 리뷰를_생성한다() {
        // given
        Long orderId = 1L;
        Order order = new Order(
                OrderState.FINISH,
                1L,
                "석연걸",
                1L,
                "김밥천국",
                "김치찜",
                10000.0
        );
        ReflectionTestUtils.setField(order, "id", orderId);

        CreateReviewRequestDto dto = new CreateReviewRequestDto("리뷰1", 5);
        Review review = Review.builder()
                .content(dto.getContent())
                .rating(dto.getRating())
                .user(reviewService.getUser(order.getUserId()))
                .shop(reviewService.getShop(order.getShopId()))
                .order(order)
                .build();

        given(orderRepository.findOrderById(orderId)).willReturn(Optional.of(order));
        given(reviewRepository.save(any())).willReturn(review);

        // when
        CreateReviewResponseDto reviews = reviewService.createReview(dto, orderId);

        // then
        assertNotNull(reviews);
    }
}