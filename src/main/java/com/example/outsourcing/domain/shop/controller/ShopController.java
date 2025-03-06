package com.example.outsourcing.domain.shop.controller;

import com.example.outsourcing.common.enums.ShopCategory;
import com.example.outsourcing.domain.auth.annotation.Auth;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.shop.dto.request.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.request.StateShopRequestDto;
import com.example.outsourcing.domain.shop.dto.response.PageShopResponseDto;
import com.example.outsourcing.domain.shop.dto.response.ShopMenuResponseDto;
import com.example.outsourcing.domain.shop.dto.response.ShopResponseDto;
import com.example.outsourcing.domain.shop.dto.response.StateShopResponseDto;
import com.example.outsourcing.domain.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    // 가게 생성
    @PostMapping
    public ResponseEntity<ShopResponseDto> addShop(
            @RequestBody ShopRequestDto requestDto,
            @Auth AuthUser authUser
    ) {
        return ResponseEntity.ok(shopService.addShop(requestDto, authUser.getId()));
    }


    // 가게 단건 조회 (상점 정보 + 메뉴 리스트)
    @GetMapping("/{shopId}")
    public ResponseEntity<ShopMenuResponseDto> getShop(@PathVariable Long shopId) {

        return ResponseEntity.ok(shopService.getShop(shopId));
    }

    // 가게 다건 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<PageShopResponseDto>> getShops(
            @RequestParam(required = false) ShopCategory category,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Auth AuthUser authUser
    ) {
        // 로그인 유무에 따른 로직 분리
        if (authUser.getId() != null) {
            // 로그인 시
            return ResponseEntity.ok(shopService.getShopsLogin(authUser.getId(), category, name, page, size));
        } else {
            // 비로그인 시
            return ResponseEntity.ok(shopService.getShops(category, name, page, size));
        }

    }

    // 가게 단건 수정
    @PatchMapping("/{shopId}")
    public ResponseEntity<ShopResponseDto> updateShop(
            @PathVariable Long shopId,
            @RequestBody ShopRequestDto requestDto,
            @Auth AuthUser authUser
    ) {
        return ResponseEntity.ok(shopService.updateShop(shopId, requestDto, authUser.getId()));
    }

    // 가게 영업상태 강제 변경
    @PatchMapping("/{shopId}/state")
    public ResponseEntity<StateShopResponseDto> updateStateShop(
            @PathVariable Long shopId,
            @RequestBody StateShopRequestDto requestDto,
            @Auth AuthUser authUser
    ) {
        return ResponseEntity.ok(shopService.updateStateShop(shopId, requestDto, authUser.getId()));
    }

    // 가게 폐업
    @DeleteMapping("/{shopId}")
    public ResponseEntity<Void> deleteShop(
            @PathVariable Long shopId,
            @Auth AuthUser authUser
    ) {
        shopService.deleteShop(shopId, authUser.getId());

        return ResponseEntity.ok().build();
    }

    // 가게 즐겨찾기
    @PostMapping("/{shopId}/bookmark")
    public ResponseEntity<Void> addBookmark(
            @PathVariable Long shopId,
            @Auth AuthUser authUser
    ) {
        shopService.addBookmark(shopId, authUser.getId());

        return ResponseEntity.ok().build();
    }

    // 가게 즐겨찾기 취소
    @PostMapping("/{shopId}/unBookmark")
    public ResponseEntity<Void> deleteBookmark(
            @PathVariable Long shopId,
            @Auth AuthUser authUser
    ) {
        shopService.deleteBookmark(shopId, authUser.getId());

        return ResponseEntity.ok().build();
    }
    
    // 즐겨찾기 가게 다건 조회
    @GetMapping("/bookmarks")
    public ResponseEntity<Page<PageShopResponseDto>> getShopBookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Auth AuthUser authUser
    ) {
            return ResponseEntity.ok(shopService.getShopBookmarks(authUser.getId(), page, size));
    }

}
