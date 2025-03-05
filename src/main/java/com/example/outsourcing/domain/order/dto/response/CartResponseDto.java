package com.example.outsourcing.domain.order.dto.response;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.domain.order.entity.Cart;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class CartResponseDto {

    private final Long id;

    private final Long shopId;

    private final PageResponseDto<CartItemResponseDto> cartItems;


    public CartResponseDto(Cart cart, Pageable pageable){
        this.id = cart.getId();
        this.shopId = cart.getShop().getId();
        this.cartItems = CartItemResponseDto.toPageCartItemDto(cart.getCartItems(), pageable);
    }

}
