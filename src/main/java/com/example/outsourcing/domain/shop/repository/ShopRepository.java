package com.example.outsourcing.domain.shop.repository;

import com.example.outsourcing.common.enums.ShopCategory;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.lang.model.element.Name;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long>, JpaSpecificationExecutor<Shop> {

    @Query("select s from Shop s where s.id = :shopId and s.deletedAt is null")
    Optional<Shop> findShopById(@Param("shopId") Long shopId);

    // 한 유저 당 폐업안한 가게 개수 카운트
    int countByUserIdAndDeletedAtIsNull(Long userId);
  
    List<Shop> findShopsByDeletedAtIsNullAndUser(User user, Pageable pageable);

}
