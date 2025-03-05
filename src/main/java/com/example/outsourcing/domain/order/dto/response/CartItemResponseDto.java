package com.example.outsourcing.domain.order.dto.response;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.domain.order.entity.Cart;
import com.example.outsourcing.domain.order.entity.CartItem;
import lombok.Getter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;


@Getter
public class CartItemResponseDto {

    private final Long id;

    private final Long menuId;

    private final String Name;

    private final Integer price;

    private final Integer quantity;

    public CartItemResponseDto(CartItem cartItem){
        this.id = cartItem.getId();
        this.menuId = cartItem.getMenu().getId();
        this.Name = cartItem.getMenu().getName();
        this.price = cartItem.getMenu().getPrice();
        this.quantity = cartItem.getQuantity();
    }

    public static CartItemResponseDto toDtoCartItem(CartItem cartItem){ return new CartItemResponseDto(cartItem); }

    public static PageResponseDto<CartItemResponseDto> toPageCartItemDto(List<CartItem> cartItems, Pageable pageable){
        return new PageResponseDto<>(new PageImpl<>(
                cartItems.stream()
                        .map(CartItemResponseDto::toDtoCartItem)
                        .toList(),
                pageable,
                cartItems.size()
        ));
    }

}
