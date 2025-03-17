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

        // 로그인 성공 시 오류 메시지 삭제
        session.removeAttribute("loginErrorMessage");

        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName(); // 로그인한 사용자 이메일
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            
            // 권한에 따라 다른 세션 키 설정
            if (authorities.stream().anyMatch(a -> 
                a.getAuthority().equals("ROLE_ADMIN") || 
                a.getAuthority().equals("ROLE_HO") || 
                a.getAuthority().equals("ROLE_BO") ||
                a.getAuthority().equals("ROLE_GUEST"))) {
                session.setAttribute("adminEmail", userEmail);  // 관리자용 세션
            } else {
                session.setAttribute("memberEmail", userEmail); // 일반 회원용 세션
            }

            // 로그인 전에 가려던 페이지가 있는지 확인
            String prevPage = (String) session.getAttribute("prevPage");

            if (prevPage != null) {
                session.removeAttribute("prevPage");  // 세션에서 삭제
                response.sendRedirect(prevPage);      // 원래 가려던 페이지로 리디렉션
                return;
            }

            // 권한에 따라 기본 리디렉션 페이지 설정
            String targetUrl = "/"; // 기본 URL

            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                targetUrl = "/admin/";  // 최고 관리자 페이지
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_HO"))) {
                targetUrl = "/ho";      // HO 관리자 페이지
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_BO"))) {
                targetUrl = "/bo";      // BO 관리자 페이지
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_GUEST"))) {
                targetUrl = "/guest";   // 관리자 승인 대기 페이지
            } else {
                targetUrl = "/member/"; // 일반 사용자
            }

            response.sendRedirect(targetUrl);
            return; // super.onAuthenticationSuccess() 실행되지 않도록 종료
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}