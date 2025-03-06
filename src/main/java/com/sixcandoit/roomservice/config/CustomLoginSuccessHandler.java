package com.sixcandoit.roomservice.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession();

        // 사용자 정보 출력
        System.out.println("사용자 이름: " + authentication.getName());
        System.out.println("사용자 권한: " + authentication.getAuthorities());

        // 사용자 추가 정보 출력
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            if (token.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) token.getPrincipal();
            }
        }

        if (session != null) {
            String userEmail = authentication.getName(); // 로그인한 사용자 이메일
            session.setAttribute("memberEmail", userEmail); // 세션 키를 memberEmail로 통일

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String targetUrl = "/"; // 기본 URL

            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                targetUrl = "/admin/"; // 최고 관리자 페이지
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_HO"))) {
                targetUrl = "/ho"; // HO 관리자 페이지
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_BO"))) {
                targetUrl = "/bo"; // BO 관리자 페이지
            } else {
                targetUrl = "/member/"; // 일반 사용자
            }

            response.sendRedirect(targetUrl);
            return; // super.onAuthenticationSuccess() 실행되지 않도록 종료
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
