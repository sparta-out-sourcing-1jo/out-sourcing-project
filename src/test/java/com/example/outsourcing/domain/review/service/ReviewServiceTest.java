package com.example.outsourcing.domain.review.service;

import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.common.enums.ShopCategory;
import com.example.outsourcing.common.enums.ShopState;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.review.dto.request.CreateReviewRequestDto;
import com.example.outsourcing.domain.review.dto.response.CreateReviewResponseDto;
import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.review.service.ReviewService;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShopRepository shopRepository;

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

//    @Test
//    void 주문이_FINISH_상태가_아닐_시_CONFLICT_에러() {
//        // given
//        Long orderId = 1L;
//        Long userId = 1L;
//        Long shopId = 1L;
//
//        User user = new User();
//        ReflectionTestUtils.setField(user, "id", userId);
//        ReflectionTestUtils.setField(user, "email", "a@a.com");
//        ReflectionTestUtils.setField(user, "password", "1234");
//        ReflectionTestUtils.setField(user, "username", "석연걸");
//        ReflectionTestUtils.setField(user, "address", "서울시");
//        ReflectionTestUtils.setField(user, "role", UserRole.USER);
//
//        Shop shop = new Shop();
//        ReflectionTestUtils.setField(shop, "id", shopId);
//        ReflectionTestUtils.setField(shop, "name", "김밥천국");
//        ReflectionTestUtils.setField(shop, "intro", "맛있어요");
//        ReflectionTestUtils.setField(shop, "address", "서울시");
//        ReflectionTestUtils.setField(shop, "category", ShopCategory.KOR);
//        ReflectionTestUtils.setField(shop, "openAt", LocalTime.now());
//        ReflectionTestUtils.setField(shop, "closeAt", LocalTime.now());
//        ReflectionTestUtils.setField(shop, "averageRating", 3.7);
//        ReflectionTestUtils.setField(shop, "reviewCount", 100);
//        ReflectionTestUtils.setField(shop, "minPrice", 15000.0);
//        ReflectionTestUtils.setField(shop, "state", ShopState.OPEN);
//        ReflectionTestUtils.setField(shop, "user", user);
//
//        Order order = new Order();
//        ReflectionTestUtils.setField(order, "id", orderId);
//        ReflectionTestUtils.setField(order, "state", OrderState.CLIENT_CANCEL);
//        ReflectionTestUtils.setField(order, "userId", userId);
//        ReflectionTestUtils.setField(order, "userName", "석연걸");
//        ReflectionTestUtils.setField(order, "shopId", shopId);
//        ReflectionTestUtils.setField(order, "shopName", "김밥천국");
//        ReflectionTestUtils.setField(order, "menuName", "김치찜");
//        ReflectionTestUtils.setField(order, "totalPrice", 10000.0);
//
//        given(userRepository.findUserById(userId)).willReturn(Optional.of(user));
//        given(shopRepository.findShopById(shopId)).willReturn(Optional.of(shop));
//
//        CreateReviewRequestDto dto = new CreateReviewRequestDto("리뷰1", 5);
//
//        given(orderRepository.findOrderById(orderId)).willReturn(Optional.of(order));
//
//        // when
//        ResponseStatusException exception = assertThrows(
//                ResponseStatusException.class,
//                () -> reviewService.createReview(dto, orderId)
//        );
//
//        // then
//        assertEquals("아직 해당 주문이 도착하지 않았습니다", exception.getMessage());
//    }

//    @Test
//    void 리뷰를_생성한다() {
//        // given
//        Long orderId = 1L;
//        Long userId = 1L;
//        Long shopId = 1L;
//
//        User user = new User();
//        ReflectionTestUtils.setField(user, "id", userId);
//        ReflectionTestUtils.setField(user, "email", "a@a.com");
//        ReflectionTestUtils.setField(user, "password", "1234");
//        ReflectionTestUtils.setField(user, "username", "석연걸");
//        ReflectionTestUtils.setField(user, "address", "서울시");
//        ReflectionTestUtils.setField(user, "role", UserRole.USER);
//
//        Shop shop = new Shop();
//        ReflectionTestUtils.setField(shop, "id", shopId);
//        ReflectionTestUtils.setField(shop, "name", "김밥천국");
//        ReflectionTestUtils.setField(shop, "intro", "맛있어요");
//        ReflectionTestUtils.setField(shop, "address", "서울시");
//        ReflectionTestUtils.setField(shop, "category", ShopCategory.KOR);
//        ReflectionTestUtils.setField(shop, "openAt", LocalTime.now());
//        ReflectionTestUtils.setField(shop, "closeAt", LocalTime.now());
//        ReflectionTestUtils.setField(shop, "averageRating", 3.7);
//        ReflectionTestUtils.setField(shop, "reviewCount", 100);
//        ReflectionTestUtils.setField(shop, "minPrice", 15000.0);
//        ReflectionTestUtils.setField(shop, "state", ShopState.OPEN);
//        ReflectionTestUtils.setField(shop, "user", user);
//
//        Order order = new Order();
//        ReflectionTestUtils.setField(order, "id", orderId);
//        ReflectionTestUtils.setField(order, "state", OrderState.FINISH);
//        ReflectionTestUtils.setField(order, "userId", userId);
//        ReflectionTestUtils.setField(order, "userName", "석연걸");
//        ReflectionTestUtils.setField(order, "shopId", shopId);
//        ReflectionTestUtils.setField(order, "shopName", "김밥천국");
//        ReflectionTestUtils.setField(order, "menuName", "김치찜");
//        ReflectionTestUtils.setField(order, "totalPrice", 10000.0);
//
//        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
//        when(shopRepository.findShopById(shopId)).thenReturn(Optional.of(shop));
//        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(order));
//
//        CreateReviewRequestDto dto = new CreateReviewRequestDto("리뷰1", 5);
//        Review review = Review.builder()
//                .content(dto.getContent())
//                .rating(dto.getRating())
//                .user(user)
//                .shop(shop)
//                .order(order)
//                .build();
//
//        given(reviewRepository.save(any())).willReturn(review);
//
//        // when
//        CreateReviewResponseDto reviews = reviewService.createReview(dto, orderId);
//
//        // then
//        assertNotNull(reviews);
//    }
}