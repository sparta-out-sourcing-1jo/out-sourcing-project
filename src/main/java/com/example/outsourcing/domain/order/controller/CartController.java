package com.example.outsourcing.domain.order.controller;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.domain.auth.annotation.Auth;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.order.dto.request.UpdateCartRequestDto;
import com.example.outsourcing.domain.order.dto.response.CartResponseDto;
import com.example.outsourcing.domain.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDto> findCart(
            @Auth AuthUser authUser,
            @PageableDefault(page = 1, size = 10, sort = "createdAt", direction = DESC) Pageable pageable
    ){
        Pageable convertPageable = PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize(), pageable.getSort());
        return ResponseEntity.ok(cartService.findCarts(authUser, convertPageable));
    }

    @PatchMapping
    public ResponseEntity<CartResponseDto> updateCart(
            @Auth AuthUser authUser,
            @RequestBody(required = false) UpdateCartRequestDto dto,
            @PageableDefault(page = 1, size = 10, sort = "createdAt", direction = DESC) Pageable pageable
    ){
        Pageable convertPageable = PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize(), pageable.getSort());
        return ResponseEntity.ok(cartService.updateCart(authUser, dto, convertPageable));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCart(
            @Auth AuthUser authUser
    ){
        cartService.deleteCart(authUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
