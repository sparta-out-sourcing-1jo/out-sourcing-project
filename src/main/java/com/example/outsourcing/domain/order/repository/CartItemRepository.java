package com.example.outsourcing.domain.order.repository;

import com.example.outsourcing.domain.order.entity.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @EntityGraph(attributePaths = {"carts"})
    List<CartItem> findAllByOrderIsNullAndDeletedAtIsNotNull();
}
