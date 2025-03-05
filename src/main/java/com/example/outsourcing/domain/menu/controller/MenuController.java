package com.example.outsourcing.domain.menu.controller;

import com.example.outsourcing.domain.auth.annotation.Auth;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.menu.dto.Response.MenuResponseDto;
import com.example.outsourcing.domain.menu.dto.Request.MenuSaveRequestDto;
import com.example.outsourcing.domain.menu.dto.Response.MenuSaveResponseDto;
import com.example.outsourcing.domain.menu.dto.Request.MenuUpdateRequestDto;
import com.example.outsourcing.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // 메뉴 생성
    @PostMapping("/menus")
    public ResponseEntity<MenuSaveResponseDto> saveMenu(@RequestBody MenuSaveRequestDto menuSaveRequestDto, @Auth AuthUser authUser, @RequestParam Long shopId) {
        return ResponseEntity.ok(menuService.saveMenu(menuSaveRequestDto, authUser.getId(), shopId));
    }

    // 식당 조회시 메뉴 전체 조회
    @GetMapping("/menus")
    public ResponseEntity<List<MenuResponseDto>> getAllMenus(@RequestParam Long shopId) {
        List<MenuResponseDto> menus = menuService.findAllMenusByShopId(shopId);
        return ResponseEntity.ok(menus);
    }

    // 메뉴 수정
    @PutMapping("/menus/{menuId}")
    public void updateMenu(@PathVariable Long menuId, @RequestBody MenuUpdateRequestDto menuUpdateRequestDto, @Auth AuthUser authUser) {
        menuService.updateMenu(menuId, menuUpdateRequestDto, authUser.getId());
    }

    // 메뉴 삭제
    @DeleteMapping("/menus/{menuId}")
    public void deleteMenu(@PathVariable Long menuId, @Auth AuthUser authUser) {
        menuService.deleteMenuById(menuId, authUser.getId());
    }
}
