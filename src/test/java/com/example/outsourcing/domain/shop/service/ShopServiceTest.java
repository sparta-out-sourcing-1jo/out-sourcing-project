package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.domain.shop.dto.request.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.response.ShopResponseDto;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.Optional;

import static com.example.outsourcing.common.enums.ShopCategory.CHICKEN;
import static com.example.outsourcing.common.enums.UserRole.OWNER;
import static com.example.outsourcing.common.enums.UserRole.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


// ExtendWith: 서비스 단위에서의 테스트, MockitoExtension 와 함께 사용
@ExtendWith(MockitoExtension.class)
public class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShopService shopService;

    @Test
    void shop_생성_성공_시_Null_을_반환하지_않고_필드값이_일치한다() {
        // given
        // 유저 생성
        User user = new User();
        Long userId = 1L;
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "username", "치킨집 사장님");
        ReflectionTestUtils.setField(user, "role", OWNER);

        // 요청 dto 생성
        ShopRequestDto requestDto = ShopRequestDto.builder()
                .name("BHC 서울점")
                .intro("여기는 BHC 입니다.")
                .address("서울시")
                .category(CHICKEN)
                .openAt(LocalTime.of(9, 30))
                .closeAt(LocalTime.of(21, 30))
                .minPrice(19000.0)
                .build();

        // LocalDateTime 는 날짜 정보까지 나타내지만 LocalTime 는 시간만 나타냄
        // TODO shop 엔티티의 오픈시간, 마감시간 타입을 LocalTime 으로 바꾸기

        // 유저 검증 강제 통과
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        ShopResponseDto add = shopService.addShop(requestDto, userId);

        // then
        assertThat(add).isNotNull();
        assertEquals(1, user.getId());
        assertEquals("치킨집 사장님", user.getUsername());
        assertEquals("OWNER", user.getRole().toString());
        assertEquals("BHC 서울점", add.getName());
        assertEquals("여기는 BHC 입니다.", add.getIntro());
        assertEquals("서울시", add.getAddress());
        assertEquals("CHICKEN", add.getCategory().toString()); // 이 부분 질문 여기는 .toString() 없어도 잘되는데 getRole 부분은 .toString() 없으면 안됨 + 여긴 .toString() 넣으면 주의 밑줄 쳐짐
        assertEquals(LocalTime.of(9, 30), add.getOpenAt());
        assertEquals(LocalTime.of(21, 30), add.getCloseAt());
        assertEquals(19000.0, add.getMinPrice());
    }


    @Test
    void shop_생성_시_user가_null인_경우_RuntimeException_발생한다(){
        // given
        // 유저 생성
        User user = new User();
        Long userId = 1L;
        ReflectionTestUtils.setField(user, "role", OWNER);

        // 요청 dto 생성
        ShopRequestDto requestDto = ShopRequestDto.builder().build();

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> shopService.addShop(requestDto, userId));

        // then
        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
    }


    @Test
    void shop_생성_시_활성화된_shop이_3개_이상인_경우_RuntimeException_발생한다(){
        // given
        // 유저 생성
        User user = new User();
        Long userId = 1L;
        ReflectionTestUtils.setField(user, "role", OWNER);

        // 요청 dto 생성
        ShopRequestDto requestDto = ShopRequestDto.builder().build();

        // 유저 검증 강제 통과
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // 활성화 된 shop 개수 3개 강제 설정
        when(shopRepository.countByUserIdAndDeletedAtIsNull(userId)).thenReturn(3);

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> shopService.addShop(requestDto, userId));

        // then
        assertEquals("가게는 최대 3개 까지만 생성 가능합니다.", exception.getMessage());
    }

    @Test
    void shop_생성_시_userRole이_OWNER가_아닌_경우_RuntimeException_발생한다() {
        // given
        // 유저 생성
        User user = new User();
        Long userId = 1L;
        ReflectionTestUtils.setField(user, "role", USER);

        // 요청 dto 생성
        ShopRequestDto requestDto = ShopRequestDto.builder().build();

        // 유저 검증 강제 통과
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> shopService.addShop(requestDto, userId));

        // then
        assertEquals("사장님만 가게 생성 가능합니다.", exception.getMessage());
    }

}
