package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.domain.shop.entity.ShopBookmark;
import com.example.outsourcing.domain.shop.repository.ShopBookmarkRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.shop.dto.request.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.request.StateShopRequestDto;
import com.example.outsourcing.domain.shop.dto.response.ShopResponseDto;
import com.example.outsourcing.domain.shop.dto.response.StateShopResponseDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

import static com.example.outsourcing.common.enums.ShopCategory.CHICKEN;
import static com.example.outsourcing.common.enums.ShopCategory.PIZZA;
import static com.example.outsourcing.common.enums.ShopState.CLOSE;
import static com.example.outsourcing.common.enums.ShopState.OPEN;
import static com.example.outsourcing.common.enums.UserRole.OWNER;
import static com.example.outsourcing.common.enums.UserRole.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


// ExtendWith: 서비스 단위에서의 테스트, MockitoExtension 와 함께 사용
@ExtendWith(MockitoExtension.class)
public class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ShopBookmarkRepository shopBookmarkRepository;

    @InjectMocks
    private ShopService shopService;

    // 생성된 객체 캡쳐용
    @Captor
    private ArgumentCaptor<ShopBookmark> bookmarkCaptor;

    // 디버그 로깅용
    Logger logger = LoggerFactory.getLogger(ShopService.class);
    
    // 공용 필드
    private Long ownerId, customerId, shopId;
    private ShopRequestDto shopRequestDto;
    private ShopRequestDto updateRequestDto;
    private User mockOwner;
    private User mockCustomer;
    private Shop mockShop;

    // 각 테스트 실행 전 선행 실행
    @BeforeEach
    void setUp() {
        // 유저 - 사장
        ownerId = 1L;
        mockOwner = new User();
        ReflectionTestUtils.setField(mockOwner, "id", ownerId);
        ReflectionTestUtils.setField(mockOwner, "username", "치킨집 사장님");
        ReflectionTestUtils.setField(mockOwner, "role", OWNER);

        // 유저 - 손님
        customerId = 2L;
        mockCustomer = new User();
        ReflectionTestUtils.setField(mockCustomer, "id", customerId);
        ReflectionTestUtils.setField(mockCustomer, "username", "치킨집 손님");
        ReflectionTestUtils.setField(mockCustomer, "role", USER);

        // 가게
        shopId = 1L;
        mockShop = new Shop();
        ReflectionTestUtils.setField(mockShop, "id", shopId);
        ReflectionTestUtils.setField(mockShop, "name", "교촌치킨 서울점");
        ReflectionTestUtils.setField(mockShop, "minPrice", 10000.0);
        ReflectionTestUtils.setField(mockShop, "user", mockOwner);
        ReflectionTestUtils.setField(mockShop, "state", CLOSE);

        // 요청 DTO
        shopRequestDto = ShopRequestDto.builder()
                .name("BHC 서울점")
                .intro("여기는 BHC 입니다.")
                .address("서울시")
                .category(CHICKEN)
                .openAt(LocalTime.of(9, 30))
                .closeAt(LocalTime.of(21, 30))
                .minPrice(19000)
                .build();

        // 수정 요청 DTO
        updateRequestDto = ShopRequestDto.builder()
                .name("도미노피자 서울점")
                .intro("도미노피자로 바꿨습니다.")
                .address("서울시")
                .category(PIZZA)
                .openAt(LocalTime.of(9, 30))
                .closeAt(LocalTime.of(21, 30))
                .minPrice(23000.0)
                .build();
    }


    // addShop 테스트
    @Test
    void shop_생성_성공_시_Null_을_반환하지_않고_필드값이_일치한다() {
        // given
        // 유저 검증 강제 통과
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(mockOwner));

        // when
        ShopResponseDto add = shopService.addShop(shopRequestDto, ownerId);

        // then
        assertThat(add).isNotNull();
        assertEquals(1, mockOwner.getId());
        assertEquals("치킨집 사장님", mockOwner.getUsername());
        assertEquals("OWNER", mockOwner.getRole().toString());
        assertEquals("BHC 서울점", add.getName());
        assertEquals("여기는 BHC 입니다.", add.getIntro());
        assertEquals("서울시", add.getAddress());
        assertEquals("CHICKEN", add.getCategory().toString());
        assertEquals(LocalTime.of(9, 30), add.getOpenAt());
        assertEquals(LocalTime.of(21, 30), add.getCloseAt());
        assertEquals(19000, add.getMinPrice());
    }


    @Test
    void shop_생성_시_user가_null인_경우_NOT_FOUND이_발생한다() {
        // given
        // 유저 검증 강제 통과 취소
//        when(userRepository.findById(ownerId)).thenReturn(Optional.of(mockOwner));

        // when
        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> shopService.addShop(shopRequestDto, ownerId));

        // then
        assertEquals("404 NOT_FOUND \"해당하는 유저를 찾을 수 없습니다.\"", e.getMessage());
    }


    @Test
    void shop_생성_시_활성화된_shop이_3개_이상인_경우_BAD_REQUEST가_발생한다() {
        // given
        // 유저 검증 강제 통과
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(mockOwner));

        // 활성화 된 shop 개수 3개 강제 설정
        when(shopRepository.countByUserIdAndDeletedAtIsNull(ownerId)).thenReturn(3);

        // when
        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> shopService.addShop(shopRequestDto, ownerId));

        // then
        assertEquals("400 BAD_REQUEST \"가게는 최대 3개까지 생성 가능합니다\"", e.getMessage());
    }

    @Test
    void shop_생성_시_user의_role이_OWNER가_아닌_경우_CONFLICT이_발생한다() {
        // given
        // 유저 검증 강제 통과
        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        // when
        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> shopService.addShop(shopRequestDto, customerId));

        // then
        assertEquals("409 CONFLICT \"사장님만 가게를 생성할 수 있습니다\"", e.getMessage());
    }


    // updateShop 테스트
    @Test
    void shop_수정_성공_시_Null_을_반환하지_않고_필드값이_일치한다() {
        // given
        // 유저 검증 강제 통과
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(mockOwner));

        // 가게 검증 강제 통과
        when(shopRepository.save(any(Shop.class))).thenReturn(mockShop);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(mockShop));

        // when
        ShopResponseDto update = shopService.updateShop(shopId, updateRequestDto, ownerId);

        // then
        assertThat(update).isNotNull();
        assertEquals(1, mockOwner.getId());
        assertEquals("치킨집 사장님", mockOwner.getUsername());
        assertEquals("OWNER", mockOwner.getRole().toString());
        assertEquals("도미노피자 서울점", update.getName());
        assertEquals("도미노피자로 바꿨습니다.", update.getIntro());
        assertEquals("서울시", update.getAddress());
        assertEquals("PIZZA", update.getCategory().toString());
        assertEquals(LocalTime.of(9, 30), update.getOpenAt());
        assertEquals(LocalTime.of(21, 30), update.getCloseAt());
        assertEquals(23000.0, update.getMinPrice());
    }


    @Test
    void shop_수정_시_사장님이_아닐경우_CONFLICT이_발생한다() {
        // given
        // 유저 검증 강제 통과
        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        // when
        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> shopService.updateShop(shopId, updateRequestDto, customerId));

        // then
        assertEquals("409 CONFLICT \"사장님만 가게를 수정할 수 있습니다\"", e.getMessage());
    }

    // updateStateShop 테스트
    @Test
    void shop_state_수정_시_Null_을_반환하지_않고_필드값이_일치한다() {
        // given
        // state 요청 dto 생성
        StateShopRequestDto requestDto = new StateShopRequestDto();
        ReflectionTestUtils.setField(requestDto, "state", OPEN);

        // 유저 검증 강제 통과
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(mockOwner));

        // 가게 검증 강제 통과
        when(shopRepository.save(any(Shop.class))).thenReturn(mockShop);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(mockShop));

        // 변경 전 상태 로깅
        logger.info("초기 영업 상태: {}", mockShop.getState());

        // when
        StateShopResponseDto updateState = shopService.updateStateShop(shopId, requestDto, ownerId);

        // then
        assertThat(updateState).isNotNull();
        assertEquals(OPEN, updateState.getState());
    }

    // deleteShop 테스트 (소프트 딜리트)
    @Test
    void shop_삭제_시_DeletedAt값이_Null이_아니다() {
        // given
        // 유저 검증 강제 통과
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(mockOwner));

        // 가게 검증 강제 통과
        when(shopRepository.save(any(Shop.class))).thenReturn(mockShop);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(mockShop));

        // when
        shopService.deleteShop(shopId, ownerId);

        // then
        assertThat(mockShop.getDeletedAt()).isNotNull();
    }

    // scheduledShopState 테스트
    @Test
    void 오픈_시간이_되면_ShopState가_OPEN이_된다() {
        // given
        // 시간 설정
        LocalTime nowTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        // 테스트용 shop 설정
        Shop openShop = Shop.builder()
                .state(CLOSE)
                .openAt(nowTime)
                .build();

        // 테스트용 shop 을 반환
        when(shopRepository.findAll()).thenReturn(Arrays.asList(openShop));

        // 변경 전 상태 로깅
        logger.info("변경전 영업 상태: {}", openShop.getState());

        // when
        shopService.scheduledShopState();

        // then
        assertEquals(OPEN, openShop.getState());
    }

    @Test
    void 마감_시간이_되면_ShopState가_CLOSE이_된다() {
        // given
        // 시간 설정
        LocalTime nowTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        // 테스트용 shop 설정
        Shop closeShop = Shop.builder()
                .state(OPEN)
                .closeAt(nowTime)
                .build();

        // 테스트용 shop 을 반환
        when(shopRepository.findAll()).thenReturn(Arrays.asList(closeShop));

        // 변경 전 상태 로깅
        logger.info("변경전 영업 상태: {}", closeShop.getState());

        // when
        shopService.scheduledShopState();

        // then
        assertEquals(CLOSE, closeShop.getState());
    }


    // addBookmark 테스트
    @Test
    void addBookmark_성공_시_객체가_생성되고_값이_일치한다(){
        //given
        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(mockShop));

        //when
        // 메서드 실행
        shopService.addBookmark(shopId, customerId);

        // 메서드가 실행 될 때, 객체 캡쳐
        verify(shopBookmarkRepository).save(bookmarkCaptor.capture());

        // 캡쳐된 객체 값 추출
        ShopBookmark capturedBookmark = bookmarkCaptor.getValue();

        //then
        Assertions.assertNotNull(capturedBookmark);
        Assertions.assertEquals(mockCustomer, capturedBookmark.getUser());
        Assertions.assertEquals(mockShop, capturedBookmark.getShop());
    }

    @Test
    void addBookmark_시_이미_북마크_되어있다면_CONFLICT_발생한다(){
        //given
        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(mockShop));

        when(shopBookmarkRepository.findByShopIdAndUserId(shopId, customerId))
                .thenReturn(Optional.of(new ShopBookmark()));

        //when
        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> shopService.addBookmark(shopId, customerId));

        //then
        assertEquals("409 CONFLICT \"이미 해당 가게를 즐겨찾기 중입니다.\"", e.getMessage());
    }

    // deleteBookmark 테스트 (하드 딜리트)
    @Test
    void deleteBookmark_성공_시_delete메서드가_성공적으로_호출된다(){
        //given
        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(mockShop));

        ShopBookmark mockBookmark = new ShopBookmark();
        when(shopBookmarkRepository.findByShopIdAndUserId(shopId, customerId))
                .thenReturn(Optional.of(mockBookmark));

        //when
        shopService.deleteBookmark(shopId, customerId);

        //then
        verify(shopBookmarkRepository).delete(mockBookmark);
    }


}


