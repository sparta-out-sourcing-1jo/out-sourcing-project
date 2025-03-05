package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.common.enums.ShopCategory;
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
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
                .category(String.valueOf(shop.getCategory())) // 이넘 값을 그대로 내보낼 수 없으니 String.valueOf 를 이용하여 이넘값의 이름을 문자열로 반환해서 추출
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
//    public ShopMenuResponseDto getShop(Long shopId) {
//
//        // 가게 검증
//        Shop shop = findShop(shopId);
//
//        // 메뉴 리스트 생성
//        List<Menu> menus = menuRepository.findAllByShop_Id(shopId);
//
//        // 가게와 메뉴를 DTO 로 변환
//        ShopResponseDto shopResponseDto = returnShopResponseDto(shop);
//        List<MenuResponseDto> menusDto = menus.stream().map(MenuResponseDto::new).collect(Collectors.toList());
//
//        return ShopMenuResponseDto.builder()
//                .shopInfo(shopResponseDto)
//                .menus(menusDto)
//                .build();
//    }


    // 가게 다건 조회 (비로그인)
    @Transactional(readOnly = true)
    public Page<PageShopResponseDto> getShops(ShopCategory category, String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 동적 쿼리 적용
        Specification<Shop> specification = Specification.where(ShopSpecification.shopDeletedAtIsNull())
                .and(ShopSpecification.shopCategoryEqual(category.toString()))
                .and(ShopSpecification.shopNameLike(name));

        Page<Shop> shops = shopRepository.findAll(specification, pageable);

        if (shops == null) {
            return Page.empty(pageable);
        }

        return shops.map(PageShopResponseDto::new);
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
        String Address2Chars = userAddress.substring(0, 2);

        // 동적 쿼리 적용
        Specification<Shop> specification = Specification.where(ShopSpecification.shopDeletedAtIsNull())
                .and(ShopSpecification.shopCategoryEqual(category.toString()))
                .and(ShopSpecification.shopNameLike(name))
                .and(ShopSpecification.shopAddressLike(Address2Chars));

        Page<Shop> shops = shopRepository.findAll(specification, pageable);

        if (shops == null) {
            return Page.empty(pageable);
        }

        return shops.map(PageShopResponseDto::new);
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
}
