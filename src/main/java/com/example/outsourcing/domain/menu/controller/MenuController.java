package com.example.outsourcing.domain.menu.controller;

import com.example.outsourcing.domain.menu.dto.MenuResponseDto;
import com.example.outsourcing.domain.menu.dto.MenuSaveRequestDto;
import com.example.outsourcing.domain.menu.dto.MenuSaveResponseDto;
import com.example.outsourcing.domain.menu.dto.MenuUpdateRequestDto;
import com.example.outsourcing.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    //메뉴 생성
    @PostMapping("/menus")
    public ResponseEntity<MenuSaveResponseDto> saveMenu(@RequestBody MenuSaveRequestDto menuSaveRequestDto, @RequestParam Long userId) {
        return ResponseEntity.ok(menuService.saveMenu(menuSaveRequestDto, userId));
    }

    //식당 조회시 메뉴 전체 조회
    @GetMapping("/menus?shopId={shopId}")
    public ResponseEntity<List<MenuResponseDto>> getAllMenus(@PathVariable Long shopId) {
        return ResponseEntity.ok(menuService.findAllMenus());
    }

    //메뉴 수정
    @PutMapping("/menus/{menuId}")
    public void updateMenu(@PathVariable Long menuId, @RequestBody MenuUpdateRequestDto menuUpdateRequestDto, @RequestParam Long userId) {
        menuService.updateMenu(menuId, menuUpdateRequestDto, userId);
    }

    @DeleteMapping("/menus/{menuId}")
    public void deleteMenu(@PathVariable Long menuId, @RequestParam Long userId) {
        menuService.deleteMenuById(menuId, userId);
    }
}
