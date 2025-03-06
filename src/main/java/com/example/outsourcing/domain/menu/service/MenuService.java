package com.example.outsourcing.domain.menu.service;

import com.example.outsourcing.common.exception.ErrorCode;
import com.example.outsourcing.domain.menu.dto.MenuResponseDto;
import com.example.outsourcing.domain.menu.dto.MenuSaveRequestDto;
import com.example.outsourcing.domain.menu.dto.MenuSaveResponseDto;
import com.example.outsourcing.domain.menu.dto.MenuUpdateRequestDto;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    //메뉴 생성
    //특정 식당에서 만드는 것으로 처리되었는지 확인 필요
    @Transactional
    public MenuSaveResponseDto saveMenu(MenuSaveRequestDto menuSaveRequestDto, Long userId) {

        //유저 검증
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage()));

        //오너 검증
        if (!user.getRole().equals("OWNER")) {
            new IllegalArgumentException(ErrorCode.USER_ACCESS_DENIED.getMessage());
        }

        //샵 소유 여부 검증
        Shop shop = shopRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.SHOP_NOT_FOUND.getMessage())
        );

        //메뉴 제작
        Menu menu = new Menu(menuSaveRequestDto.getName(), menuSaveRequestDto.getPrice(),0);
        Menu savedMenu = menuRepository.save(menu);

        return new MenuSaveResponseDto(
                savedMenu.getId(),
                savedMenu.getName(),
                menuSaveRequestDto.getPrice()
        );
    }

    //메뉴 조회
    @Transactional(readOnly = true)
    public List<MenuResponseDto> findAllMenus() {
        List<Menu> menus = menuRepository.findAll();
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

    //메뉴 수정
    // 에러 코드 출력 방식 제대로 작성한 것인지 확인 필요
    @Transactional
    public void updateMenu(Long menuId, MenuUpdateRequestDto menuUpdateRequestDto, Long userId) {

        //유저 검증
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage()));

        //오너 검증
        if (!user.getRole().equals("OWNER")) {
            new IllegalArgumentException(ErrorCode.USER_ACCESS_DENIED.getMessage());
        }

        //메뉴 검증
        Menu menu = menuRepository.findMenuById(menuId).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.MENU_NOT_FOUND.getMessage())
        );

        if (!menu.getShop().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("이 메뉴는 당신의 가게에 속하지 않습니다.");
        }

        menu.updateMenu(menuUpdateRequestDto);
    }

    //메뉴 삭제
    @Transactional
    public void deleteMenuById(Long menuId, Long userId) {

        //유저 검증
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage()));

        //오너 검증
        if (!user.getRole().equals("OWNER")) {
            new IllegalArgumentException(ErrorCode.USER_ACCESS_DENIED.getMessage());
        }

        //메뉴 검증
        Menu menu = menuRepository.findMenuById(menuId).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.MENU_NOT_FOUND.getMessage())
        );

        if (!menu.getShop().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("이 메뉴는 당신의 가게에 속하지 않습니다.");
        }

        //소프트 딜리트
        menu.setDeletedAt();

        //삭제된 정보 repository에 저장
        menuRepository.save(menu);
    }
}
