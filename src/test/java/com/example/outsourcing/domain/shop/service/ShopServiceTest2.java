package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.shop.dto.response.PageShopResponseDto;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.server.ResponseStatusException;

import static com.example.outsourcing.common.enums.ShopCategory.CHICKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


// 스프링 부트 테스트를 위한 테스트 메서드
@SpringBootTest
public class ShopServiceTest2 {
    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ShopService shopService;

    // getShops 테스트
    @Test
    void shop_다건_조회_시_null을_반환_하지_않는다() {
        // given

        // when
        Page<PageShopResponseDto> page = shopService.getShops(CHICKEN, "손님", 0, 10);

        // then
        assertThat(page).isNotNull();
    }

    // getShopsLogin 테스트
    @Test
    void 로그인하고_shop_다건_조회_시_user가_null인_경우_RuntimeException이_발생한다() {
        // given

        // when
        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> shopService.getShopsLogin(1L, CHICKEN, "손님", 0, 10));

        // then
        assertEquals("404 NOT_FOUND \"해당하는 유저를 찾을 수 없습니다.\"", e.getMessage());
    }
}
