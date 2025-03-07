package com.example.outsourcing.domain.order.repository;

import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.entity.Cart;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static com.example.outsourcing.common.enums.OrderState.*;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Order savedOrder;
    private User mockUser;
    private Shop mockShop;
    private Menu mockMenu;

    @BeforeEach
    @Transactional
    void setUp(){
        // User 생성
        mockUser = new User();
        ReflectionTestUtils.setField(mockUser, "username", "testUser");
        ReflectionTestUtils.setField(mockUser, "role", UserRole.USER);
        ReflectionTestUtils.setField(mockUser, "email", "a@a.com");
        ReflectionTestUtils.setField(mockUser, "password", "1");
        ReflectionTestUtils.setField(mockUser, "address", "1");
        mockUser = userRepository.save(mockUser);

        // Shop 생성
        mockShop = new Shop();
        ReflectionTestUtils.setField(mockShop, "name", "testShop");
        ReflectionTestUtils.setField(mockShop, "minPrice", 10000);
        ReflectionTestUtils.setField(mockShop, "address", "1");
        ReflectionTestUtils.setField(mockShop, "user", mockUser);
        mockShop = shopRepository.save(mockShop);

        // Menu 생성
        mockMenu = new Menu();
        ReflectionTestUtils.setField(mockMenu, "name", "testMenu");
        ReflectionTestUtils.setField(mockMenu, "price", 10000);
        ReflectionTestUtils.setField(mockMenu, "shop", mockShop);
        mockMenu = menuRepository.save(mockMenu);

        // 최소한의 Cart 생성 (빈 cartItem 목록, totalPrice 설정)
        Cart cart = new Cart(mockUser, mockShop, new ArrayList<>());
        ReflectionTestUtils.setField(cart, "id", 1L);
        ReflectionTestUtils.setField(cart, "totalPrice", 10000);

        // Order 생성: Cart를 기반으로 생성
        Order newOrder = new Order(CLIENT_ACCEPT, mockUser, cart);
        savedOrder = orderRepository.save(newOrder);
    }

    @Test
    void 주문번호로_주문_단건을_조회할_수_있다(){
        // when
        Order retrieveOrder = orderRepository.findOrderById(savedOrder.getId()).orElse(null);
        // then
        assertNotNull(retrieveOrder);
        assertNull(retrieveOrder.getDeletedAt());
    }

    @Test
    void 주문_목록_조회_JPA_TEST(){
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        // when
        Page<Order> orders = orderRepository.findOrdersByUser(mockUser, pageable);
        // then
        assertFalse(orders.isEmpty());
        assertNull(orders.getContent().get(0).getDeletedAt());
        assertEquals(savedOrder, orders.getContent().get(0));
    }
}
