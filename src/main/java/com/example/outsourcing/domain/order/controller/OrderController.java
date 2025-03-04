package com.example.outsourcing.domain.order.controller;

import com.example.outsourcing.common.aop.annotation.Order;
import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.order.dto.request.CreateOrderRequestDto;
import com.example.outsourcing.domain.order.dto.request.UpdateOrderRequestDto;
import com.example.outsourcing.domain.order.dto.response.OrderResponseDto;
import com.example.outsourcing.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문생성
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder (
            @RequestParam Long userId, String role,
            @RequestBody CreateOrderRequestDto dto
    ){
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        OrderResponseDto savedOrder = orderService.createOrder(userId, userRole, dto);
        return ResponseEntity.ok(savedOrder);
    }

    // 주문단건조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> findOrder(
            @RequestParam Long userId, String role,
            @PathVariable Long orderId
    ){
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        OrderResponseDto findOrder = orderService.findOrder(userId, userRole, orderId);
        return ResponseEntity.ok(findOrder);
    }

    // 주문 목록 조회
    @GetMapping
    public ResponseEntity<PageResponseDto<OrderResponseDto>> findOrders(
            @RequestParam Long userId, String role,
            @RequestParam(required = false) Long shopId, OrderState orderState,
            @PageableDefault(page = 1, size = 10, sort = "updatedAt", direction = DESC) Pageable pageable
    ){
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        Pageable convertPageable = PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize(), pageable.getSort());
        PageResponseDto<OrderResponseDto> findOrders = orderService.findOrders(userId, userRole, shopId, orderState, convertPageable);
        return ResponseEntity.ok(findOrders);
    }

    // 주문 갱신
    // @Order 붙여서 AOP에서 logging함.
    @Order
    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @RequestParam String role, Long shopId,
            @PathVariable Long orderId,
            @RequestBody UpdateOrderRequestDto dto
    ){
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        OrderResponseDto updateOrder = orderService.updateOrder(userRole, shopId, orderId, dto);
        return ResponseEntity.ok(updateOrder);
    }
}
