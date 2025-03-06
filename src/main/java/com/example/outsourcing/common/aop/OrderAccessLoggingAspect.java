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
import java.lang.reflect.Method;
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

        // PathVariable 로 전달된 orderId 찾기
        String orderId = extractPathVariable(joinPoint, "orderId").orElse("N/A");

        // 요청 시각 기록
        String requestTime = LocalDateTime.now().toString();

        // 로깅
        log.info("[Order API Access] shopId: {}, orderId: {}, requestTime: {}", shopId, orderId, requestTime);

        // 원래 메서드 실행
        return joinPoint.proceed();
    }

    // @Pathvariable 을 추출하기 위한 매서드
    private Optional<String> extractPathVariable(ProceedingJoinPoint joinPoint, String variableName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        return IntStream.range(0, paramNames.length)
                .filter(i -> paramNames[i].equals(variableName))
                .mapToObj(i -> args[i].toString())
                .findFirst();
    }
}
