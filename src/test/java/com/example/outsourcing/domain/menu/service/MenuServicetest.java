package com.example.outsourcing.domain.menu.service;


import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.menu.controller.MenuController;
import com.example.outsourcing.domain.menu.dto.Response.MenuResponseDto;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.shop.dto.request.ShopRequestDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;

import static com.example.outsourcing.common.enums.ShopCategory.CHICKEN;
import static com.example.outsourcing.common.enums.UserRole.OWNER;
import static org.hamcrest.MatcherAssert.assertThat;


@ExtendWith(MockitoExtension.class)
public class MenuServicetest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private MenuService menuService;


    @Test
    @DisplayName("메뉴 제작 검증")
    void menuServiceSaveTest() {

        //given
        User user = new User("test@example.com", "testPassword", "testName", "testAddress", UserRole.OWNER);
        User justUser = new User("test2@example.com", "testPassword2", "testName2", "testAddress2", UserRole.USER);

        ShopRequestDto requestDto = ShopRequestDto.builder()
                .name("BHC 서울점")
                .intro("여기는 BHC 입니다.")
                .address("서울시")
                .category(CHICKEN)
                .openAt(LocalTime.of(9, 30))
                .closeAt(LocalTime.of(21, 30))
                .minPrice(19000.0)
                .build();

        Menu menu = new Menu("fried", 15000);

        //when
        MenuResponseDto findAllMenu = menuService.findAllMenus();
        //then
        assertThat(findAllMenu);
    }
}
