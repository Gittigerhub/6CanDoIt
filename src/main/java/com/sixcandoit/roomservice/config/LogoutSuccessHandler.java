package com.sixcandoit.roomservice.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        //로그아웃시 섹션을 제거한다.
        if(session != null) {
            session.invalidate();
        }

        super.setDefaultTargetUrl("/");
        super.onLogoutSuccess(request, response, authentication);
    }
}

