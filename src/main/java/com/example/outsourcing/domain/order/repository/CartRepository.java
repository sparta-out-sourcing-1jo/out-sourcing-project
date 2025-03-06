package com.example.outsourcing.domain.order.repository;

import com.example.outsourcing.domain.order.entity.Cart;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


public interface CartRepository extends JpaRepository<Cart, Long> {

    boolean existsCartByUserAndDeletedAtIsNull(User user);

    Cart findCartByUserAndDeletedAtIsNull(User user);


    @EntityGraph(attributePaths = {"orders"})
    List<Cart> findCartsByShopIn(Collection<Shop> shops);
}
