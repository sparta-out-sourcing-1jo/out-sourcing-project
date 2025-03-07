package com.example.outsourcing.common.filter;

import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.common.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtFilterTest {

    private JwtUtil jwtUtil;
    private JwtFilter jwtFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain chain;

    private static final String SECRET_KEY = "4asdkkladasd13slkasdlkasdjhfjklasdfhlkdjsahflhksdf";

    @BeforeEach
    void setup() throws Exception {
        jwtUtil = new JwtUtil();
        Field secretKeyField = JwtUtil.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtUtil, SECRET_KEY);

        jwtUtil.init();
        jwtFilter = new JwtFilter(jwtUtil);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
    }

    @Test
    void auth_API는_JwtFilter에서_JWT를_검증하지_않음() throws ServletException, IOException {
        //given
        request.setRequestURI("/auth/login");

        //when
        jwtFilter.doFilter(request, response, chain);

        //then
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    void 일반_API는_JwtFilter에서_JWT가_없으면_BAD_REQUEST를_던진다() throws ServletException, IOException {
        //given
        request.setRequestURI("/users");

        //when
        jwtFilter.doFilter(request, response, chain);

        //then
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals("JWT 토큰이 필요합니다.", response.getErrorMessage());
    }

    @Test
    void 일반_API는_JwtFilter에서_USER_권한_JWT가_존재하면_통과한다() throws ServletException, IOException {
        //given
        request.setRequestURI("/users");

        Long userId = 1L;
        String email = "test@example.com";
        UserRole userRole = UserRole.USER;
        String token = jwtUtil.createToken(userId, email, userRole);
        request.addHeader("Authorization", token);

        //when
        jwtFilter.doFilter(request, response, chain);

        //then
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals(userId, request.getAttribute("userId"));
        assertEquals(email, request.getAttribute("email"));
        assertEquals(UserRole.USER.name(), request.getAttribute("userRole"));
        assertNotNull(chain.getRequest());
    }

    @Test
    void 일반_API는_JwtFilter에서_OWNER_권한_JWT가_존재하면_통과한다() throws ServletException, IOException {
        //given
        request.setRequestURI("/users");

        Long userId = 1L;
        String email = "test@example.com";
        UserRole userRole = UserRole.OWNER;
        String token = jwtUtil.createToken(userId, email, userRole);
        request.addHeader("Authorization", token);

        //when
        jwtFilter.doFilter(request, response, chain);

        //then
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals(userId, request.getAttribute("userId"));
        assertEquals(email, request.getAttribute("email"));
        assertEquals(UserRole.OWNER.name(), request.getAttribute("userRole"));
        assertNotNull(chain.getRequest());
    }

    @Test
    void ADMIN_API는_JwtFilter에서_ADMIN_권한_JWT가_존재하면_통과한다() throws ServletException, IOException {
        //given
        request.setRequestURI("/admin/test");

        String token = jwtUtil.createToken(1L, "test@example.com", UserRole.ADMIN);
        request.addHeader("Authorization", token);

        //when
        jwtFilter.doFilter(request, response, chain);

        //then
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    void ADMIN_API는_JwtFilter에서_ADMIN_권한_JWT가_아니라면_FORBIDDEN을_던진다() throws ServletException, IOException {
        //given
        request.setRequestURI("/admin/dashboard");

        String token = jwtUtil.createToken(1L, "test@example.com", UserRole.USER);
        request.addHeader("Authorization", token);

        //when
        jwtFilter.doFilter(request, response, chain);

        //then
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        assertEquals("관리자 권한이 없습니다.", response.getErrorMessage());
    }

    @Test
    void 유효하지_않은_JWT는_JwtFilter를_통과하지_못한다() throws ServletException, IOException {
        //given
        request.setRequestURI("/api/users");
        request.addHeader("Authorization", "Bearer invalid.token.format");

        //when
        jwtFilter.doFilter(request, response, chain);

        //then
        assertTrue(response.getStatus()>=400);
    }
}