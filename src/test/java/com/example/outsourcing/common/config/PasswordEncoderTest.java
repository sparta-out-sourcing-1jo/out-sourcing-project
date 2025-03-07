package com.example.outsourcing.common.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class PasswordEncoderTest {
    @InjectMocks
    private PasswordEncoder passwordEncoder;

    @Test
    void matches_정상적인_동작(){
        //given
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        //when
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        //then
        assertTrue(matches);
    }
}