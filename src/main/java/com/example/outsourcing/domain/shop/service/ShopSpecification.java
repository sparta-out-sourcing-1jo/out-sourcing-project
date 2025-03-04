package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.domain.shop.entity.Shop;
import org.springframework.data.jpa.domain.Specification;

// 동적 쿼리 생성
public class ShopSpecification {

    // 폐업 필터링
    public static Specification<Shop> shopDeletedAtIsNull() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // 카테고리 분류 필터링
    public static Specification<Shop> shopCategoryEqual(String category) {
        if (category == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    // 이름 검색 필터링
    public static Specification<Shop> shopNameLike(String name) {
        if (name == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%");
    }

    // 주소 필터링
    public static Specification<Shop> shopAddressLike(String address) {
        if (address == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.like(root.get("address"), "%" + address + "%");
    }

}
