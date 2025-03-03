package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.domain.shop.dto.request.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.response.ShopResponseDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.outsourcing.common.enums.UserRole.OWNER;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    // 가게 생성
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
}
