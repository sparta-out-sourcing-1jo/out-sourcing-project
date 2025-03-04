package com.example.outsourcing.domain.order.repository;

import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.id = :orderId and o.deletedAt is null")
    Optional<Order> findOrderById(@Param("orderId") Long orderId);

    @Override
    Optional<Order> findById(Long orderId);

    Page<Order> findOrdersByUserAndState(User user, OrderState state, Pageable pageable);

    Page<Order> findOrdersByUserAndShopAndState(User user, Shop shop, OrderState state, Pageable pageable);

    Page<Order> findOrdersByShopIn(Collection<Shop> shops, Pageable pageable);

    Page<Order> findOrdersByShopInAndState(Collection<Shop> shops, OrderState state, Pageable pageable);

    Page<Order> findOrdersByShop(Shop shop, Pageable pageable);

    Page<Order> findOrdersByShopAndState(Shop shop, OrderState state, Pageable pageable);

    Page<Order> findOrdersByUser(User user, Pageable pageable);

    Page<Order> findOrdersByUserAndShop(User user, Shop shop, Pageable pageable);

}