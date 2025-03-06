package com.example.outsourcing.domain.menu.service;

import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.common.exception.ErrorCode;
import com.example.outsourcing.domain.menu.dto.response.MenuResponseDto;
import com.example.outsourcing.domain.menu.dto.request.MenuSaveRequestDto;
import com.example.outsourcing.domain.menu.dto.response.MenuSaveResponseDto;
import com.example.outsourcing.domain.menu.dto.request.MenuUpdateRequestDto;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    // 유저 검증 메소드
    private User validateUser(Long userId) {
        return userRepository.findUserById(userId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND.getMessage())
        );
    }

    // 오너 검증 메소드
    private void validateOwner(User user) {
        log.info("Validating owner: userId={}, role={}", user.getId(), user.getRole());
        if (user.getRole() != UserRole.OWNER) {
            log.warn("User is not an OWNER. Access denied.");
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    ErrorCode.USER_ACCESS_DENIED.getMessage()
            );
        }
    }

    // 샵 소유 검증 메소드
    private Shop validateShop(Long shopId) {
        Shop shop = shopRepository.findShopById(shopId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SHOP_NOT_FOUND.getMessage()
                )
        );
        log.info("Validated shop: shopId={}, ownerId={}", shop.getId(), shop.getUser().getId());
        return shop;
    }

    // 메뉴 검증 메소드
    private Menu validateMenu(Long menuId) {
        return menuRepository.findMenuById(menuId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.MENU_NOT_FOUND.getMessage()
                )
        );
    }

    // 메뉴가 속한 샵 검증 메소드
    private void validateMenuBelongsToShop(Menu menu, Long userId) {
        if (!menu.getShop().getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    ErrorCode.MENU_NOT_IN_SHOP.getMessage()
            );
        }
    }

    // 메뉴 생성
    @Transactional
    public MenuSaveResponseDto saveMenu(MenuSaveRequestDto menuSaveRequestDto, Long userId, Long shopId) {

        // 유저 검증
        User user = validateUser(userId);

        // 오너 검증
        validateOwner(user);

        // 샵 소유 여부 검증
        Shop shop = validateShop(shopId);

        // 메뉴 제작
        Menu menu = new Menu(menuSaveRequestDto.getName(), menuSaveRequestDto.getPrice(), shop);
        Menu savedMenu = menuRepository.save(menu);

        return new MenuSaveResponseDto(
                savedMenu.getId(),
                savedMenu.getName(),
                menuSaveRequestDto.getPrice()
        );
    }

    // 메뉴 조회
    @Transactional(readOnly = true)
    public List<MenuResponseDto> findAllMenusByShopId(Long shopId) {
        Shop shop = validateShop(shopId);

        List<Menu> menus = menuRepository.findAllByShopId(shopId);
        List<MenuResponseDto> menuResponseDtos = new ArrayList<>();

        for (Menu menu : menus) {
            menuResponseDtos.add(new MenuResponseDto(
                    menu.getId(),
                    menu.getName(),
                    menu.getPrice(),
                    menu.getOrderCount()
            ));
        }

        return menuResponseDtos;
    }

    // 메뉴 수정
    @Transactional
    public void updateMenu(Long menuId, MenuUpdateRequestDto menuUpdateRequestDto, Long userId) {

        // 유저 검증
        User user = validateUser(userId);

        // 오너 검증
        validateOwner(user);

        // 메뉴 검증
        Menu menu = validateMenu(menuId);

        // 메뉴가 속한 샵 검증
        validateMenuBelongsToShop(menu, userId);

        menu.updateMenu(menuUpdateRequestDto);
    }

    // 메뉴 삭제
    @Transactional
    public void deleteMenuById(Long menuId, Long userId) {

        // 유저 검증
        User user = validateUser(userId);

        // 오너 검증
        validateOwner(user);

        // 메뉴 검증
        Menu menu = validateMenu(menuId);

        // 메뉴가 속한 샵 검증
        validateMenuBelongsToShop(menu, userId);

        // 소프트 딜리트
        menu.setDeletedAt();

        // 삭제된 정보 repository에 저장
        menuRepository.save(menu);
    }
}