package com.example.outsourcing.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Aspect
@Component
public class OrderAccessLoggingAspect {

    // @Order 이 붙은 매서드만 pointcut 지정
    @Around("@annotation(com.example.outsourcing.common.aop.annotation.Order)")
    public Object logOrderApiAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed(); // 요청 정보가 없으면 그냥 실행
        }

        HttpServletRequest request = attributes.getRequest();

        // 쿼리 파라미터에서 shopId 가져오기
        String shopId = Optional.ofNullable(request.getParameter("shopId")).orElse("N/A");

        // PathVariable 로 전달된 orderId 찾기; parameter 이름이 없으면 URI에서 추출
        String orderId = extractPathVariable(joinPoint, "orderId")
                .orElseGet(() -> extractOrderIdFromUri(request.getRequestURI()));

        // 요청 시각 기록
        String requestTime = LocalDateTime.now().toString();

        // 로깅
        log.info("[Order API Access] shopId: {}, orderId: {}, requestTime: {}", shopId, orderId, requestTime);

        // 원래 메서드 실행
        return joinPoint.proceed();
    }

    // 기존 방법: 메서드 파라미터 이름으로 추출 (컴파일 옵션에 따라 실패할 수 있음)
    private Optional<String> extractPathVariable(ProceedingJoinPoint joinPoint, String variableName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        return IntStream.range(0, paramNames.length)
                .filter(i -> variableName.equals(paramNames[i]))
                .mapToObj(i -> args[i].toString())
                .findFirst();
    }

    // URI에서 마지막 segment를 orderId로 간주하여 추출
    private String extractOrderIdFromUri(String uri) {
        if (uri == null || uri.isEmpty()) return "N/A";
        String[] segments = uri.split("/");
        return segments.length > 0 ? segments[segments.length - 1] : "N/A";
    }
}
