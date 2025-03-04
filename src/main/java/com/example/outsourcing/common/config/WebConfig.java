package com.example.outsourcing.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 컨버터 등록.
    @Override
    public void addFormatters(FormatterRegistry registry){
        registry.addConverter(new OrderStateConverter());
        registry.addConverter(new CancelReasonConverter());
    }
}
