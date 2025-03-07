package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.common.enums.ShopState;
import com.example.outsourcing.common.enums.ShopCategory;
import com.example.outsourcing.domain.menu.dto.response.MenuResponseDto;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.shop.dto.request.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.request.StateShopRequestDto;
import com.example.outsourcing.domain.shop.dto.response.PageShopResponseDto;
import com.example.outsourcing.domain.shop.dto.response.ShopMenuResponseDto;
import com.example.outsourcing.domain.shop.dto.response.ShopResponseDto;
import com.example.outsourcing.domain.shop.dto.response.StateShopResponseDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.entity.ShopBookmark;
import com.example.outsourcing.domain.shop.repository.ShopBookmarkRepository;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.outsourcing.common.enums.UserRole.OWNER;
import static com.example.outsourcing.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    private final ShopBookmarkRepository shopBookmarkRepository;

    // 가게 생성
    @Transactional
    public ShopResponseDto addShop(ShopRequestDto requestDto, Long userId) {

        // 유저 검증
        User user = findUser(userId);

        // 사장 검증
        if (!user.getRole().equals(OWNER)) {
            throw new ResponseStatusException(
                    OWNER_CAN_CREATE.getStatus(),
                    OWNER_CAN_CREATE.getMessage()
            );
        }

        // 가게 개수 검증
        if (shopRepository.countByUserIdAndDeletedAtIsNull(userId) >= 3) {
            throw new ResponseStatusException(
                    MAX_SHOP_COUNT.getStatus(),
                    MAX_SHOP_COUNT.getMessage()
            );
        }

        // 받은 dto 를 바탕으로 DB에 저장할 shop 객체 생성
        Shop shop = Shop.builder()
                .user(user)
                .name(requestDto.getName())
                .intro(requestDto.getIntro())
                .address(requestDto.getAddress())
                .category(requestDto.getCategory())
                .openAt(requestDto.getOpenAt())
                .closeAt(requestDto.getCloseAt())
                .minPrice(requestDto.getMinPrice())
                .build();

        // DB에 위에서 생성한 shop 객체를 저장
        shopRepository.save(shop);

        return returnShopResponseDto(shop);
    }


    // DTO 리턴용 리팩터링 메서드
    private ShopResponseDto returnShopResponseDto(Shop shop) {

        return ShopResponseDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .intro(shop.getIntro())
                .address(shop.getAddress())
                .category(shop.getCategory())
                .openAt(shop.getOpenAt())
                .closeAt(shop.getCloseAt())
                .averageRating(getAverageRating(shop.getId()))
                .reviewCount(reviewRepository.countByShop_IdAndDeletedAtIsNull(shop.getId()))
                .minPrice(shop.getMinPrice())
                .state(shop.getState())
                .owner(shop.getUser().getUsername())
                .createAt(shop.getCreatedAt())
                .updateAt(shop.getUpdatedAt())
                .build();
    }

    // 평균 별점 구하기 메서드
    public Double getAverageRating(Long shopId) {
        int reviewCount = reviewRepository.countByShop_IdAndDeletedAtIsNull(shopId);
        int totalRating = reviewRepository.findSumRatingByShopId(shopId);

        // 0 으로 나누면 ArithmeticException 발생
        if (reviewCount == 0) {
            return 0.0;
        }

        return (double) (totalRating / reviewCount);
    }

    // 가게 단건 조회
    public ShopMenuResponseDto getShop(Long shopId) {

        // 가게 검증
        Shop shop = findShop(shopId);

        // 메뉴 리스트 생성
        List<Menu> menus = menuRepository.findAllByShopId(shopId);

        // 가게와 메뉴를 DTO 로 변환
        ShopResponseDto shopResponseDto = returnShopResponseDto(shop);
        List<MenuResponseDto> menusDto = menus.stream().map(MenuResponseDto::of).collect(Collectors.toList());

        return ShopMenuResponseDto.builder()
                .shopInfo(shopResponseDto)
                .menus(menusDto)
                .build();
    }

    // 가게 다건 조회 (비로그인)
    @Transactional(readOnly = true)
    public Page<PageShopResponseDto> getShops(ShopCategory category, String name, int page, int size) {

        // 페이지 값
        Pageable pageable = PageRequest.of(page, size);

        // 동적 쿼리 적용
        Specification<Shop> specification = Specification
                .where(ShopSpecification.shopDeletedAtIsNull())
                .and(ShopSpecification.shopCategoryEqual(category))
                .and(ShopSpecification.shopNameLike(name));

        Page<Shop> shops = shopRepository.findAll(specification, pageable);

        return shops.map(shop -> new PageShopResponseDto(shop, getAverageRating(shop.getId())));
    }

    // 가게 다건 조회 (로그인)
    @Transactional(readOnly = true)
    public Page<PageShopResponseDto> getShopsLogin(Long userId, ShopCategory category, String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 유저 검증
        User user = findUser(userId);

        // 유저 주소 추출
        String userAddress = user.getAddress();

        // 추출된 주소 중 앞의 2글자만 추출
        String AddressChars = userAddress.substring(0, 2);

        // 동적 쿼리 적용
        Specification<Shop> specification = Specification.where(ShopSpecification.shopDeletedAtIsNull())
                .and(ShopSpecification.shopCategoryEqual(category))
                .and(ShopSpecification.shopNameLike(name))
                .and(ShopSpecification.shopAddressLike(AddressChars));

        Page<Shop> shops = shopRepository.findAll(specification, pageable);

        return shops.map(shop -> new PageShopResponseDto(shop, getAverageRating(shop.getId())));
    }

    // 가게 정보 수정
    public ShopResponseDto updateShop(Long shopId, ShopRequestDto requestDto, Long userId) {

        // 유저 검증
        User user = findUser(userId);

        // 사장 검증
        if (!user.getRole().equals(OWNER)) {
            throw new ResponseStatusException(
                    OWNER_CAN_UPDATE.getStatus(),
                    OWNER_CAN_UPDATE.getMessage()
            );
        }

        // 가게 검증
        Shop shop = findShop(shopId);

        // shop 엔티티 데이터 업데이트
        shop.update(requestDto);

        // 업데이트된 엔티티를 DB에 저장하고 동시에 반환
        return returnShopResponseDto(shopRepository.save(shop));
    }

    public StateShopResponseDto updateStateShop(Long shopId, StateShopRequestDto requestDto, Long userId) {

        // 유저 검증
        User user = findUser(userId);

        // 사장 검증
        if (!user.getRole().equals(OWNER)) {
            throw new ResponseStatusException(
                    OWNER_CAN_UPDATE.getStatus(),
                    OWNER_CAN_UPDATE.getMessage()
            );
        }

        // 가게 검증
        Shop shop = findShop(shopId);

        // 가게 상태 업데이트
        shop.updateState(requestDto.getState());

        // 업데이트된 엔티티를 DB에 저장
        Shop upadteShop = shopRepository.save(shop);

        // DB에 저장된 엔티티를 DTO로 반환
        return new StateShopResponseDto(upadteShop.getState(), upadteShop.getUpdatedAt());
    }


    // 가게 폐업
    public void deleteShop(Long shopId, Long userId) {
        // 유저 검증
        User user = findUser(userId);

        // 사장 검증
        if (!user.getRole().equals(OWNER)) {
            throw new ResponseStatusException(
                    OWNER_CAN_UPDATE.getStatus(),
                    OWNER_CAN_UPDATE.getMessage()
            );
        }

        // 가게 검증
        Shop shop = findShop(shopId);

        // 가게 삭제 (소프트 딜리트)
        shop.setDeletedAt();

        // 삭제된 정보 DB에 저장
        shopRepository.save(shop);

        // 반환값 없으므로 패스
    }

    // 유저 검증 메서드
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                USER_NOT_FOUND.getStatus(),
                                USER_NOT_FOUND.getMessage()
                        )
                );
    }

    private Shop findShop(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                SHOP_NOT_FOUND.getStatus(),
                                SHOP_NOT_FOUND.getMessage()
                        )
                );
    }


    // 스케줄링 메서드
    // 크론 안쓰고 이렇게도 가능: (fixedRate = 1000 * 60 * 1): 1분마다 실행 (밀리초 * 초 * 분)
    // 크론 표현식: 초 분 시 일 월 요일 => 0 * * * * *: 매요일 매월 매일 매시 매분 0초마다 반복
    @Scheduled(cron = "0 * * * * *")
    public void scheduledShopState() {
        List<Shop> shops = shopRepository.findAll();
        LocalTime nowTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES); // 초 무시하고 분 단위로 기록

        // 설정 시간과 현재 시간이 정확히 일치하는 순간에 작동
        for (Shop shop : shops) {
            if (nowTime.equals(shop.getOpenAt()) && shop.getState() != ShopState.OPEN) {
                shop.updateState(ShopState.OPEN);
            }
            if (nowTime.equals(shop.getCloseAt()) && shop.getState() != ShopState.CLOSE) {
                shop.updateState(ShopState.CLOSE);
            }
        }
    }

    // 가게 즐겨찾기
    @Transactional
    public void addBookmark(Long shopId, Long userId) {
        // 유저 검증
        User user = findUser(userId);

        // 가게 검증
        Shop shop = findShop(shopId);

        // 즐겨찾기 중복 검증
        if (shopBookmarkRepository.findByShopIdAndUserId(shopId, userId).isPresent()) {
            throw new ResponseStatusException(
                    ALREADY_EXIST_BOOKMARK.getStatus(),
                    ALREADY_EXIST_BOOKMARK.getMessage()
            );
        }

        // 엔티티 생성
        ShopBookmark shopBookmark = ShopBookmark.builder()
                .user(user)
                .shop(shop)
                .build();

        shopBookmarkRepository.save(shopBookmark);
    }

    // 즐겨찾기 삭제 (하드 딜리트)
    @Transactional
    public void deleteBookmark(Long shopId, Long userId) {

        // 유저 검증
        User user = findUser(userId);

        // 가게 검증
        Shop shop = findShop(shopId);

        // 즐겨찾기 중복 검증
        ShopBookmark bookmark = shopBookmarkRepository.findByShopIdAndUserId(shopId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                                NOT_EXIST_BOOKMARK.getStatus(),
                                NOT_EXIST_BOOKMARK.getMessage()
                        )
                );

        shopBookmarkRepository.delete(bookmark);

    }

    @Transactional(readOnly = true)
    public Page<PageShopResponseDto> getShopBookmarks(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 유저 검증
        User user = findUser(userId);

        // 특정 유저의 북마크 페이징 조회
        Page<ShopBookmark> bookmarks = shopBookmarkRepository.findByUserId(user.getId(), pageable);

        return bookmarks.map(bookmark -> new PageShopResponseDto(bookmark.getShop(), getAverageRating(bookmark.getShop().getId())));
    }

}
