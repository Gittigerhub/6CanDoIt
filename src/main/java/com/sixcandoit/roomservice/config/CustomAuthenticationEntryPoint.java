package com.sixcandoit.roomservice.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

//검증 실패했을 때 사용자 클래스
//생략가능(필요할 때만 사용)
@Component
public class CustomAuthenticationEntryPoint  implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        //prevPage(이전 페이지)를 세션에 저장한 후 로그인 페이지로 리디렉트하는 방식.
        HttpSession session = request.getSession();
        String redirectUrl = request.getRequestURI();

        // 현재 요청된 URL을 세션에 저장 (로그인 후 원래 페이지로 이동)
        session.setAttribute("prevPage", redirectUrl);

        // 오류 메시지를 세션에 저장
        session.setAttribute("loginErrorMessage", "인증 실패! 로그인 후 이용해주세요.");

        // 로그인 페이지로 리디렉트
        // 관리자 페이지 요청인지 확인 후 로그인 페이지 분기 처리
        if (redirectUrl.startsWith("/admin")) {
            response.sendRedirect("/admin/login"); // 관리자 로그인 페이지로 이동
        } else {
            response.sendRedirect("/member/login"); // 일반 사용자 로그인 페이지로 이동
        }

    }

}