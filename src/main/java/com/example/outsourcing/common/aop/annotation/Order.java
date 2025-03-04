package com.example.outsourcing.common.aop.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Order { // @Order 이 붙은 매서드 특정.
}
