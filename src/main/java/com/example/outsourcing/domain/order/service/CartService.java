package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.common.enums.ShopState;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.order.dto.request.UpdateCartRequestDto;
import com.example.outsourcing.domain.order.dto.response.CartResponseDto;
import com.example.outsourcing.domain.order.entity.Cart;
import com.example.outsourcing.domain.order.entity.CartItem;
import com.example.outsourcing.domain.order.repository.CartItemRepository;
import com.example.outsourcing.domain.order.repository.CartRepository;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.example.outsourcing.common.exception.ErrorCode.*;
import static com.example.outsourcing.common.exception.ErrorCode.SHOP_NOT_FOUND;
import static com.example.outsourcing.common.util.Util.*;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional(readOnly = true)
    public CartResponseDto findCarts(AuthUser authUser, Pageable pageable) {
        ifTrueThenThrow(
                !UserRole.USER.equals(authUser.getUserRole()),
                USER_ACCESS_DENIED
        );

        User user = userRepository.findUserById(authUser.getId()).orElseThrow(
                () -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));
        Cart cart = cartRepository.findCartByUserAndDeletedAtIsNull(user);

        return new CartResponseDto(cart, pageable);
    }

    @Transactional
    public CartResponseDto updateCart(AuthUser authUser, UpdateCartRequestDto dto, Pageable pageable) {
        // 유저 검증
        ifTrueThenThrow (
                !UserRole.USER.equals(authUser.getUserRole()),
                USER_ACCESS_DENIED
        );

        User user = userRepository.findUserById(authUser.getId()).orElseThrow(
                () -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));
        Menu menu = menuRepository.findMenuById(dto.getMenuId()).orElseThrow(
                () -> new ResponseStatusException(MENU_NOT_FOUND.getStatus(), MENU_NOT_FOUND.getMessage()));
        Shop shop = shopRepository.findShopById(dto.getShopId()).orElseThrow(
                () -> new ResponseStatusException(SHOP_NOT_FOUND.getStatus(), SHOP_NOT_FOUND.getMessage()));

        // 1. 장바구니가 없는 경우 생성
        if (!cartRepository.existsCartByUserAndDeletedAtIsNull(user)) {
            return createNewCart(user, shop, menu, dto.getQuantity(), pageable);
        }

        Cart cart = cartRepository.findCartByUserAndDeletedAtIsNull(user);

        // 2. 장바구니의 가게와 요청받은 가게가 다른경우 초기화 후 장바구니 업데이트
        if (!cart.getShop().equals(shop)) {
            return updateCartIfShopNotMatches(cart, shop, menu, dto.getQuantity(), pageable);
        }

        // 3. 장바구니에 같은 메뉴가 있으면 개수 업데이트, 없으면 생성 후 carts 에 추가
        updateOrCreateMenuInCart(cart, user, shop, menu, dto.getQuantity(), pageable);

        return new CartResponseDto(cart, pageable);
    }

    @Transactional
    public void deleteCart(AuthUser authUser) {
        ifTrueThenThrow(
                !UserRole.USER.equals(authUser.getUserRole()),
                USER_ACCESS_DENIED
        );

        User user = userRepository.findUserById(authUser.getId()).orElseThrow(
                () -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));
        Cart cart = cartRepository.findCartByUserAndDeletedAtIsNull(user);
        cart.setDeletedAt();
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteHardToCart(){
        List<CartItem> cartItems = cartItemRepository.findAllByOrderIsNullAndDeletedAtIsNotNull();
        LocalDateTime nowTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS); // 초 무시하고 분 단위로 기록
        // 설정 시간과 현재 시간이 정확히 일치하는 순간에 작동
        for (CartItem cartItem : cartItems) {
            LocalDateTime deletedAt = cartItem.getCart().getDeletedAt();
            if (deletedAt != null && Duration.between(deletedAt, nowTime).toHours() >= 24) {
                if (cartItem.getCart() != null){
                    cartRepository.deleteById(cartItem.getCart().getId());
                }
                cartItemRepository.deleteById(cartItem.getId());
            }
        }
    }


    // 1. 장바구니가 없는 경우 생성
    private CartResponseDto createNewCart(User user, Shop shop, Menu menu, Integer quantity, Pageable pageable) {
        CartItem newCartItem = new CartItem(menu, quantity);
        CartItem cartItem = cartItemRepository.save(newCartItem);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        Cart newCart = new Cart(user, shop, cartItems);
        Cart cart = cartRepository.save(newCart);

        cart.updateTotalPrice(cartItems);
        cartItem.updateCart(newCart);

        return new CartResponseDto(cart, pageable);
    }

    // 2. 같은 가게의 장바구니가 있는 경우 업데이트
    private CartResponseDto updateCartIfShopNotMatches(Cart cart, Shop shop, Menu menu, Integer quantity, Pageable pageable) {
        cart.updateShop(shop);
        cart.getCartItems().forEach(cartItem -> cartItem.updateItem(menu, quantity));
        cart.updateTotalPrice(cart.getCartItems());
        cart.getCartItems().forEach(cartItem -> cartItem.updateCart(cart));

        return new CartResponseDto(cart, pageable);
    }

    // 3. 장바구니에 같은 메뉴가 있으면 개수 업데이트, 없으면 생성 후 carts 에 추가
    private void updateOrCreateMenuInCart(Cart cart, User user, Shop shop, Menu menu, Integer quantity, Pageable pageable) {
        cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getMenu().equals(menu))
                .findAny()
                .ifPresentOrElse(
                        cartItem -> {
                            cartItem.updateQuantity(quantity);
                            cart.updateTotalPrice(cart.getCartItems());
                        },
                        () -> {
                            CartItem newCartItem = new CartItem(menu, quantity);
                            cart.getCartItems().add(newCartItem);
                            cart.updateTotalPrice(cart.getCartItems());
                        }
                );
    }

}
