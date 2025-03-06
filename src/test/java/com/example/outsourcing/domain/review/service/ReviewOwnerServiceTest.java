package com.example.outsourcing.domain.review.service;

import com.example.outsourcing.common.enums.ShopState;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.owner.review.service.OwnerReviewService;
import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
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

import java.util.Optional;

import static com.example.outsourcing.common.enums.OrderState.FINISH;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewOwnerServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OwnerReviewService reviewOwnerService;

    @Test
    void 사장_리뷰_답글_생성() {
        // given
        Long userId = 1L;
        Long userId2 = 2L;
        Long shopId = 1L;
        Long menuId = 1L;
        Long orderId = 1L;
        Long reviewId = 1L;
        Long reviewId2 = 2L;

        User user = new User("a@a.com", "Asdf1234!", "이름이요", "서울시", UserRole.OWNER);
        ReflectionTestUtils.setField(user, "id", userId);

        User user2 = new User("b@b.com", "Asdf1234@", "이름", "서울시", UserRole.USER);
        ReflectionTestUtils.setField(user2, "id", userId2);

        when(userRepository.findUserByIdOrElseThrow(userId)).thenReturn(user);
        when(userRepository.findUserByIdOrElseThrow(userId2)).thenReturn(user2);

        Shop shop = Shop.builder()
                .name("김밥천국")
                .address("서울시")
                .state(ShopState.OPEN)
                .user(user)
                .build();
        ReflectionTestUtils.setField(shop, "id", shopId);

        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", menuId);
        ReflectionTestUtils.setField(menu, "name", "김치찌개");
        ReflectionTestUtils.setField(menu, "shop", shop);

        when(shopRepository.findShopById(shopId)).thenReturn(Optional.of(shop));
        when(menuRepository.findMenuById(menuId)).thenReturn(Optional.of(menu));

        Order order = new Order(FINISH, user2, shop, menu);
        ReflectionTestUtils.setField(order, "id", orderId);

        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(order));

        Review review = Review.builder()
                .content("내용")
                .rating(5)
                .user(user2)
                .shop(shop)
                .order(order)
                .build();
        ReflectionTestUtils.setField(review, "id", reviewId);

        
    }
}
