package com.example.outsourcing.domain.order.controller;

import com.example.outsourcing.common.aop.annotation.Order;
import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.domain.auth.annotation.Auth;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.order.dto.request.UpdateOrderRequestDto;
import com.example.outsourcing.domain.order.dto.response.OrderResponseDto;
import com.example.outsourcing.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.*;
import static com.example.outsourcing.common.util.Util.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문생성
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder (
            @Auth AuthUser authUser,
            @PageableDefault(page = 1, size = 10, sort = "updatedAt", direction = DESC) Pageable pageable
    ){
        OrderResponseDto savedOrder = orderService.createOrder(authUser, convertPageable(pageable));
        return ResponseEntity.ok(savedOrder);
    }

    // 주문 단건 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> findOrder(
            @Auth AuthUser authUser,
            @PathVariable Long orderId,
            @PageableDefault(page = 1, size = 10, sort = "updatedAt", direction = DESC) Pageable pageable
    ){
        return ResponseEntity.ok(orderService.findOrder(authUser, orderId, convertPageable(pageable)));
    }

    // 주문 다건 조회
    @GetMapping
    public ResponseEntity<PageResponseDto<OrderResponseDto>> findOrders(
            @Auth AuthUser authUser,
            @RequestParam(required = false) OrderState orderState, Long shopId,
            @PageableDefault(page = 1, size = 10, sort = "updatedAt", direction = DESC) Pageable pageable
    ){
        return ResponseEntity.ok(orderService.findOrders(authUser, orderState, shopId, convertPageable(pageable)));
    }

    // 주문 갱신
    // @Order 붙여서 AOP에서 logging함.
    @Order
    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @Auth AuthUser authUser,
            @PathVariable Long orderId,
            @RequestParam Long shopId,
            @RequestBody UpdateOrderRequestDto dto,
            @PageableDefault(page = 1, size = 10, sort = "updatedAt", direction = DESC) Pageable pageable
    ){
        return ResponseEntity.ok(orderService.updateOrder(authUser, shopId, orderId, dto, convertPageable(pageable)));
    }
}
