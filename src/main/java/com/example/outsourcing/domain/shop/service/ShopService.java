package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.common.enums.ShopCategory;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.shop.dto.request.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.response.PageShopResponseDto;
import com.example.outsourcing.domain.shop.dto.response.ShopMenuResponseDto;
import com.example.outsourcing.domain.shop.dto.response.ShopResponseDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.outsourcing.common.enums.UserRole.OWNER;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    // 가게 생성
    @Transactional
    public ShopResponseDto addShop(ShopRequestDto requestDto, Long userId) {

        // 유저 검증
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 사장 검증
        if (!user.getRole().equals(OWNER)) {
            throw new RuntimeException("사장님만 가게 생성 가능합니다.");
        }

        // 가게 개수 검증
        if (shopRepository.countByUserIdAndDeletedAtIsNull(userId) >= 3) {
            throw new RuntimeException("가게는 최대 3개 까지만 생성 가능합니다.");
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
                .averageRating(shop.getAverageRating())
                .reviewCount(shop.getReviewCount())
                .minPrice(shop.getMinPrice())
                .state(shop.getState())
                .owner(shop.getUser().getUsername())
                .createAt(shop.getCreatedAt())
                .updateAt(shop.getUpdatedAt())
                .build();
    }

//    // 가게 단건 조회
//    public ShopMenuResponseDto getShop(Long shopId) {
//
//        // 가게 검증
//        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new RuntimeException("가게를 찾을 수 없습니다."));
//
//        // 메뉴 리스트 생성
//        List<Menu> menus = menuRepository.findAllByShop_Id(shopId);
//
//        // 가게와 메뉴를 DTO 로 변환
//        ShopResponseDto shopResponseDto = returnShopResponseDto(shop);
//        List<MenuResponseDto> menusDto = menus.stream().map(this::MenuResponsDto).collect(Collectors.toList());
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
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

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
}
