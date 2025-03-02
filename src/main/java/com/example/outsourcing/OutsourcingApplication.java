package com.example.outsourcing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OutsourcingApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutsourcingApplication.class, args);
    }

}
