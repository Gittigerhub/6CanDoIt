package com.sixcandoit.roomservice.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession();

        //System.out.println(authentication.getPrincipal());
        //3. 기본제공하는 정보 및 추가적인 정보를 섹션등을 이용해서 활용한다.
        // 사용자 정보 출력
        System.out.println("사용자 이름: " + authentication.getName());
        System.out.println("사용자 권한: " + authentication.getAuthorities());
        // 사용자 추가 정보 출력
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            if (token.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) token.getPrincipal();
//                System.out.println("호텔: " + userDetails.getHotels());
                // 패스워드는 출력하지 않는 것이 보안상 좋습니다.
            }
        }


        if(session != null) {
            String userid = authentication.getName();
            session.setAttribute("userid", userid);

            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));
            if(isUser) { //USER이면
                System.out.println("사용자 페이지 이동");
                super.setDefaultTargetUrl("/member/"); // 회원 메인 페이지 이동
            }

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            if(isAdmin) { //ADMIN이면
                System.out.println("관리자 페이지 이동");
                super.setDefaultTargetUrl("/admin/"); //관리자 메인 페이지 이동
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}