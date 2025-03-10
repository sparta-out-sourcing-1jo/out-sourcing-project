package com.example.outsourcing.common.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 유저 관련 예외 코드
    USER_EMAIL_DUPLICATION("다른 유저와 이메일이 중복됩니다.", CONFLICT),
    USER_NAME_DUPLICATION("다른 유저와 이름이 중복됩니다.", CONFLICT),
    USER_NOT_LOGIN("로그인이 필요합니다. 로그인을 해주세요.", UNAUTHORIZED),
    USER_NOT_FOUND("해당하는 유저를 찾을 수 없습니다.", NOT_FOUND),
    INVALID_PASSWORD("패스워드가 올바르지 않습니다.", BAD_REQUEST),
    PASSWORD_SAME_AS_OLD("이전 패스워드와 동일할 수 없습니다.", BAD_REQUEST),
    USER_ACCESS_DENIED("사용자가 접근할 수 있는 권한이 없습니다.", FORBIDDEN),
    INVALID_AUTH_COMBINATION("@Auth와 AuthUser 타입은 함께 사용되어야 합니다.", UNAUTHORIZED),
    USER_ROLE_SAME_AS_OLD("이전 역활과 동일할 수 없습니다.", BAD_REQUEST),
    INVALID_USER_ROLE("유효하지 않는 role 입니다.", BAD_REQUEST),

    // 토큰 관련 예외 코드
    TOKEN_NOT_FOUND("해당 토큰을 찾을 수 없습니다.", NOT_FOUND),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", UNAUTHORIZED),
    // 메뉴 관련 예외 코드
    MENU_NOT_FOUND("해당 메뉴를 찾을 수 없습니다.", NOT_FOUND),
    MENU_NOT_IN_SHOP("해당 메뉴는 해당 가게에서 속하지 않습니다.", BAD_REQUEST),

    // 가게 관련 예외 코드
    SHOP_NOT_FOUND("해당 가게를 찾을 수 없습니다", NOT_FOUND),
    OWNER_CAN_CREATE("사장님만 가게를 생성할 수 있습니다", CONFLICT),
    OWNER_CAN_UPDATE("사장님만 가게를 수정할 수 있습니다", CONFLICT),
    MAX_SHOP_COUNT("가게는 최대 3개까지 생성 가능합니다", BAD_REQUEST),
    ALREADY_EXIST_BOOKMARK("이미 해당 가게를 즐겨찾기 중입니다.", CONFLICT),
    NOT_EXIST_BOOKMARK("해당 가게 즐겨찾기 중이 아닙니다.", NOT_FOUND),

    // 주문 관련 예외 코드
    ORDER_NOT_FINISH("주문이 완료되지 않았습니다", CONFLICT),
    INVALID_PRICE("최소 주문 가격을 넘겨주세요.", BAD_REQUEST),
    OVER_TIME_TO_OPEN("가게오픈시간이 아닙니다.", BAD_REQUEST),
    ORDER_NOT_FOUND("해당 주문을 찾을 수 없습니다.", NOT_FOUND),
    CART_ALREADY_EXIST("장바구니가 이미 존재합니다.", BAD_REQUEST),
    DUPLICATION_TO_SHOP("같은 가게에서만 주문 할 수 있습니다.", BAD_REQUEST),
    CART_NOT_FOUND("장바구니가 비어있습니다.", BAD_REQUEST),
    
    // 리뷰 관련 예외 코드
    FILE_UPLOAD_FAILED("이미지 파일 업로드에 실패했습니다.", PRECONDITION_FAILED),
    REVIEW_NOT_FOUND("해당 리뷰를 찾을 수 없습니다", NOT_FOUND),
    REVIEW_ALREADY_EXIST("해당 주문에 대한 리뷰가 이미 존재합니다", BAD_REQUEST);
    
    private final String message;
    private final HttpStatus status;

    public void apply(HttpServletResponse response) {
        response.setStatus(status.value());
    }
}