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
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.id = :orderId and o.deletedAt is null")
    Optional<Order> findOrderById(@Param("orderId") Long orderId);

    @Override
    Optional<Order> findById(Long orderId);


    List<Order> findOrdersByShopIn(Collection<Shop> shops);

    List<Order> findOrdersByUser(User user);

    List<Order> findOrdersByUserAndState(User user, OrderState state);

    List<Order> findOrdersByShop(Shop shop);

    List<Order> findOrdersByShopAndState(Shop shop, OrderState state);

    List<Order> findOrdersByStateAndShopIn(OrderState state, Collection<Shop> shops);

    Page<Order> findOrdersByUser(User user, Pageable pageable);
}