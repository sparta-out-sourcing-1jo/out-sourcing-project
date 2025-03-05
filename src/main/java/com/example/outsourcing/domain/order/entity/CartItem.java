package com.example.outsourcing.domain.order.entity;

import com.example.outsourcing.common.entity.BaseTimeEntity;
import com.example.outsourcing.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "cart_items")
public class CartItem extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    private String name;

    private Integer price;

    private Integer quantity;


    public CartItem(Menu menu, Integer quantity){
        this.menu = menu;
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.quantity = quantity;
    }

    public void updateQuantity(Integer quantity){
        this.quantity = quantity;
    }

    public void updateCart(Cart cart) {
        this.cart = cart;
    }

    public void updateOrder(Order order){
        this.order = order;
    }

    public void updateItem(Menu menu, Integer quantity){
        this.menu = menu;
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.quantity = quantity;
    }

}
