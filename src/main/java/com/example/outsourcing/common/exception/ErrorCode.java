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

    // 메뉴 관련 예외 코드
    MENU_NOT_FOUND("해당 메뉴를 찾을 수 없습니다.", NOT_FOUND),

    // 가게 관련 예외 코드
    SHOP_NOT_FOUND("해당 가게를 찾을 수 없습니다", NOT_FOUND),

    // 주문 관련 예외 코드
    ORDER_NOT_FOUND("해당 주문을 찾을 수 없습니다.", NOT_FOUND);


    private final String message;
    private final HttpStatus status;

    public void apply(HttpServletResponse response) {
        response.setStatus(status.value());
    }
}
