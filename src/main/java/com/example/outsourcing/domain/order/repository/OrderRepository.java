package com.example.outsourcing.domain.order.repository;

import com.example.outsourcing.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.id = :orderId and o.deletedAt is null")
    Optional<Order> findOrderById(@Param("orderId") Long orderId);
}