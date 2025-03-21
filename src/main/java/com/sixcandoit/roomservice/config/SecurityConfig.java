package com.sixcandoit.roomservice.config;

import com.sixcandoit.roomservice.service.admin.AdminLoginService;
import com.sixcandoit.roomservice.service.member.MemberLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final AuthenticationEntryPoint customAuthenticationEntryPoint;

    //암호
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //관리자 로그인 서비스
    @Bean
    public AdminLoginService adminLoginService() {
        return new AdminLoginService();
    }

    //일반 로그인 서비스
    @Bean
    public MemberLoginService memberLoginService() {
        return new MemberLoginService();
    }

    //관리자 로그인처리 등록
    @Bean
    public DaoAuthenticationProvider adminProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminLoginService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    //일반 로그인처리 등록
    @Bean
    public DaoAuthenticationProvider memberProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(memberLoginService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {
        //사용권한
        http//.addFilterBefore(userAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .securityMatcher("/admin/**")
                .authorizeHttpRequests((auth)-> {
                    auth.requestMatchers("/", "/assets/**", "/css/**", "/js/**", "/img/**", "/images/**", "/favicon.ico","/error").permitAll();
                    auth.requestMatchers("/h2-console/**").permitAll();
                    auth.requestMatchers("/admin/login").permitAll();
                    auth.requestMatchers("/admin/adminlist","/admin/holist").permitAll();
                    //ajax 허용
                    auth.requestMatchers("/member/checkEmail", "/admin/checkEmail",
                            "/member/checkEmailCode", "/admin/checkEmailCode", "/admin/sendEmailCode", "/member/sendEmailCode").permitAll();
                    auth.requestMatchers("/member/checkPhone", "/admin/checkPhone").permitAll();
                    auth.requestMatchers("/login", "/logout", "/member/register", "/admin/register","/member/password","/admin/password").permitAll();
                    auth.requestMatchers("/member/**").hasAnyRole("ADMIN", "HO","BO", "MEMBER");
                    auth.requestMatchers("/admin/**", "/member/**").hasAnyRole("ADMIN","HO","BO");
                    // 조직-매장 페이지 접근 권한
                    auth.requestMatchers("/office/**", "/office/organ/**", "/office/shopdetail/**").permitAll();
                    // 이벤트-멤버포인트 페이지 접근 권한
                    auth.requestMatchers("/event/**", "/event/memberpoint/**", "/event/usermemberpoint/**","/event/userevent/**").permitAll();
                    // QnA 페이지 접근 권한
                    auth.requestMatchers("/qna/qnalist/**").hasAnyRole("ADMIN", "HO", "BO");
                    auth.requestMatchers("/qna/list", "/qna/read", "/qna/register", "/qna/update", "/qna/delete").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");
                    auth.requestMatchers("/qna/favYn/update").hasAnyRole("ADMIN", "HO", "BO");
                    auth.requestMatchers("/qna/adminread").hasAnyRole("ADMIN", "HO", "BO");
                    auth.requestMatchers("/reply/**").hasAnyRole("ADMIN", "HO", "BO");
                    // 룸 관리, 룸 예약 페이지 접근 권한
                    auth.requestMatchers("/room/member/list").permitAll();
                    auth.requestMatchers("/room/ho/**", "/res/ho/**").hasAnyRole("ADMIN", "HO");
                    auth.requestMatchers("/room/bo/**", "/res/bo/**").hasAnyRole("ADMIN", "HO", "BO");
                    auth.requestMatchers("/res/**").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");
                    auth.requestMatchers("/room/**").permitAll();
                    // 공지사항 페이지 접근 권한
                    auth.requestMatchers("/notice/list","/notice/read", "/notice/register" , "/notice/update","/notice/delete/**").hasAnyRole("ADMIN");
                    auth.requestMatchers("/notice/ho/**").hasAnyRole("ADMIN","HO");
                    auth.requestMatchers("/notice/bo/**").hasAnyRole("ADMIN","HO","BO");
                    auth.requestMatchers("/notice/userlist" , "/notice/userread").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");
                    // 오더 페이지 접근 권한
                    auth.requestMatchers("/orders/**", "/orders/payment/**", "/payment/**").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");
                    // 메뉴 페이지 접근 권한
                    auth.requestMatchers("/menu/**").permitAll();
                    // 장바구니 페이지 접근 권한
                    auth.requestMatchers("/cart/**").permitAll();
                    // 광고 페이지 접근 권한
                    auth.requestMatchers("/advertisement/**", "/advertisement/update/**").permitAll();
                    // 이미지 컨트롤러 접근 권한
                    auth.requestMatchers("/images/**").permitAll();
                    // ho, bo 권한 설정
                    auth.requestMatchers("/ho/**").hasAnyRole("ADMIN","HO");
                    auth.requestMatchers("/bo/**").hasAnyRole("ADMIN","HO","BO");
                    auth.requestMatchers("/guest/**").hasAnyRole("ADMIN","HO","BO","GUEST");
                });

        //관리자회원 로그인
        http.formLogin(login -> login
                        .defaultSuccessUrl("/admin/", true)
                        .failureUrl("/admin/login?error=true")
                        .loginPage("/admin/login")
                        .usernameParameter("adminEmail")
                        .permitAll()
                        .successHandler(customLoginSuccessHandler))                 // 커스텀 로그인 성공 핸들러 적용
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)); // 로그인 필요 시 원래 페이지 저장


        //CSRF 보호를 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        //로그아웃
        http.logout(logout-> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login")); //로그아웃

        //관리자인 경우 관리자 로그인처리
        http.authenticationProvider(adminProvider());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain2(HttpSecurity http) throws Exception {
        //사용권한
        http.authorizeHttpRequests((auth)-> {
            auth.requestMatchers("/", "/assets/**", "/css/**", "/js/**", "/img/**", "/images/**", "/favicon.ico","/error").permitAll();
            auth.requestMatchers("/h2-console/**").permitAll();
            auth.requestMatchers("/admin/updateRole").permitAll();
            auth.requestMatchers("/login", "/logout", "/member/register", "/admin/register","/member/password","/admin/password").permitAll();
            auth.requestMatchers("/admin/adminlist","/admin/holist").permitAll();

            auth.requestMatchers("/member/checkPhone", "/admin/checkPhone",
                    "/admin/sendEmailCode", "/member/sendEmailCode", "/admin/checkEmailCode","/member/checkEmailCode").permitAll();
            auth.requestMatchers("/member/checkEmail", "/admin/checkEmail").permitAll();
            auth.requestMatchers("/member/**").hasAnyRole("ADMIN","HO","BO", "MEMBER");
            auth.requestMatchers("/admin/**", "/member/**").hasAnyRole("ADMIN","HO","BO");
            // 조직-매장 페이지 접근 권한
            auth.requestMatchers("/office/**", "/office/organ/**", "/office/shopdetail/**").permitAll();
            // 이벤트-멤버포인트 페이지 접근 권한
            auth.requestMatchers("/event/**", "/event/memberpoint/**", "/upload/**","/event/update/**","/event/delete","/event/user/**").permitAll();
            // QnA 페이지 접근 권한
            auth.requestMatchers("/qna/qnalist/**").hasAnyRole("ADMIN", "HO", "BO");
            auth.requestMatchers("/qna/list", "/qna/read", "/qna/register", "/qna/update", "/qna/delete").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");
            auth.requestMatchers("/qna/favYn/update").hasAnyRole("ADMIN", "HO", "BO");
            auth.requestMatchers("/qna/adminread").hasAnyRole("ADMIN", "HO", "BO");
            auth.requestMatchers("/reply/**").hasAnyRole("ADMIN", "HO", "BO");
            // 룸 관리, 룸 예약 페이지 접근 권한
            auth.requestMatchers("/room/member/list").permitAll();
            auth.requestMatchers("/room/ho/**", "/res/ho/**").hasAnyRole("ADMIN", "HO");
            auth.requestMatchers("/room/bo/**", "/res/bo/**").hasAnyRole("ADMIN", "HO", "BO");
            auth.requestMatchers("/res/**").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");
            auth.requestMatchers("/room/**").permitAll();
            // 공지사항 페이지 접근 권한
            auth.requestMatchers("/notice/list","/notice/read", "/notice/register" , "/notice/update","/notice/delete/**").hasAnyRole("ADMIN","HO","BO");
            auth.requestMatchers("/notice/ho/**").hasAnyRole("ADMIN","HO");
            auth.requestMatchers("/notice/bo/**").hasAnyRole("ADMIN","HO","BO");
            auth.requestMatchers("/notice/userlist" , "/notice/userread").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");
            // 오더 페이지 접근 권한
            auth.requestMatchers("/orders/**", "/orders/payment/**", "/payment/**").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");
            // 메뉴 페이지 접근 권한
            auth.requestMatchers("/menu/**").permitAll();
            // 장바구니 페이지 접근 권한
            auth.requestMatchers("/cart/**").permitAll();
            // 광고 페이지 접근 권한
            auth.requestMatchers("/advertisement/**", "/advertisement/update/**").permitAll();
            // 이미지 컨트롤러 접근 권한
            auth.requestMatchers("/images/**").permitAll();
            // ho, bo, guest 권한 설정
            auth.requestMatchers("/ho/**").hasAnyRole("ADMIN","HO");
            auth.requestMatchers("/bo/**").hasAnyRole("ADMIN","HO","BO");
            auth.requestMatchers("/guest/**").hasAnyRole("ADMIN","HO","BO","GUEST");

        });

        //http.exceptionHandling(exceptionHandling ->exceptionHandling
        //         .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        // );

        //일반회원 로그인
        http.formLogin(login -> login
                        .defaultSuccessUrl("/member/", true)
                        .failureUrl("/member/login?error=true")
                        .loginPage("/member/login")
                        .usernameParameter("memberEmail")
                        .permitAll()
                        .successHandler(customLoginSuccessHandler))                 // 커스텀 로그인 성공 핸들러 적용
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)); // 로그인 필요 시 원래 페이지 저장

        //CSRF 보호를 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        //로그아웃
        http.logout(logout-> logout
                .logoutUrl("/member/logout")
                .logoutSuccessUrl("/member/login")); //로그아웃

        //일반 사용자인 경우 일반 사용자로그인 처리
        http.authenticationProvider(memberProvider());
        return http.build();
    }

}