package com.example.outsourcing.domain.shop.controller;

import com.example.outsourcing.domain.shop.dto.request.AddShopRequestDto;
import com.example.outsourcing.domain.shop.dto.response.ShopResponseDto;
import com.example.outsourcing.domain.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
//    private final MemberService memberService

    // TODO: jwt 인증 추가
    // 가게 생성
    @PostMapping
    public ResponseEntity<ShopResponseDto> addShop(@RequestBody AddShopRequestDto requestDto) {

        return ResponseEntity.ok(shopService.addShop(requestDto));
    }

    // 가게 단건 조회
    @GetMapping("/{shopId}")
    public ResponseEntity<ShopResponseDto> getShop(@PathVariable Long shopId) {

        return ResponseEntity.ok(shopService.getShop(shopId));
    }

    // TODO: 제네릭으로 만들기
    // 가게 다건 조회
    @GetMapping

    // 가게 단건 수정

    // TODO: 기본 변경은 스케줄
    // 가게 영업상태 강제 변경

    // 가게 폐업


}
