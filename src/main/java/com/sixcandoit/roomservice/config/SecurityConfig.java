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
public class SecurityConfig {

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
                .authorizeHttpRequests((auth) -> {
                    auth.requestMatchers("/", "/assets/**", "/css/**", "/js/**", "/img/**", "/images/**", "/favicon.ico", "/error").permitAll();
                    auth.requestMatchers("/h2-console/**").permitAll();

                    // 메인 페이지
                    auth.requestMatchers("/admin/").hasRole("ADMIN");
                    auth.requestMatchers("/ho/**").hasAnyRole("ADMIN", "HO");
                    auth.requestMatchers("/bo/**").hasAnyRole("ADMIN", "HO", "BO");
                    auth.requestMatchers("/guest/**").hasAnyRole("ADMIN", "HO", "BO", "GUEST");
                    auth.requestMatchers("/member/").permitAll();

                    // 로그인, 회원가입, 회원정보 관련
                    auth.requestMatchers("/admin/login", "/member/login", "/admin/logout", "/member/logout").permitAll();
                    auth.requestMatchers("/admin/password", "/member/password", "/admin/register", "/member/register",
                            "/admin/checkEmail", "/member/checkEmail", "/admin/checkPhone", "/member/checkPhone",
                            "/admin/sendEmailCode", "/member/sendEmailCode", "/admin/checkEmailCode", "/member/checkEmailCode").permitAll();
                    auth.requestMatchers("/admin/verify", "/admin/modify", "/admin/modifypw", "/admin/deleteAdmin", "/admin/deleteMember").hasAnyRole("ADMIN", "HO", "BO");
                    auth.requestMatchers("/member/verify", "/member/modify", "/member/modifypw", "/member/deleteMember").hasRole("MEMBER");
                    auth.requestMatchers("/member/mypage").hasRole("MEMBER");

                    // 회원 목록 관련
                    auth.requestMatchers("/admin/adminlist", "/admin/memberlist").hasRole("ADMIN");
                    auth.requestMatchers("/admin/holist", "/admin/updateRole").hasAnyRole("ADMIN", "HO");

                    // 전체
                    auth.requestMatchers("/member/**").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");
                    auth.requestMatchers("/admin/**").hasAnyRole("ADMIN", "HO", "BO");

                    // 공지사항 페이지 접근 권한
                    auth.requestMatchers("/notice/list", "/notice/read", "/notice/register", "/notice/update").hasAnyRole("ADMIN");
                    auth.requestMatchers("/notice/bo/**").hasAnyRole("ADMIN", "HO");
                    auth.requestMatchers("/notice/ho/**").hasAnyRole("ADMIN", "HO", "BO");
                    auth.requestMatchers("/notice/userlist", "/notice/userread").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");

                    // 오더 페이지 접근 권한
                    auth.requestMatchers("/orders/**", "/orders/payment/**", "/payment/**").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");

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

                    // 메뉴, 카트 페이지 접근 권한 - RequestMapping 되어 있어서 미작성

                    // 이벤트 및 포인트 페이지 접근 권한
                    auth.requestMatchers("/event/**").permitAll();
                    auth.requestMatchers("/event/event/**", "/event/eventread/**", "/event/eventregister/**", "/event/eventupdate", "event/memberpoint/**").hasAnyRole("ADMIN", "HO", "BO");
                    auth.requestMatchers("/event/userevent/**", "/event/usereventread/**", "/event/usermemberpoint/**").hasRole("MEMBER");

                    /*--------------------------------------------------------------------------------------------------------------------------*/

                    // 조직-매장 페이지 접근 권한
                    auth.requestMatchers("/office/list", "/office/organ", "/office/shopdetail/realread").hasRole("ADMIN");
                    auth.requestMatchers("/office/ho/list", "/office/organ/ho", "/shopdetail/realread/ho").hasRole("HO");
                    auth.requestMatchers("/office/bo/list", "/office/shopdetail/realread/bo").hasRole("BO");
                    auth.requestMatchers("/office/member/list").hasRole("MEMBER");
                    auth.requestMatchers("/office/search/list", "/office/search/hotels/list",
                            "/office/organ/register", "/office/organ/read", "/office/organ/update", "/office/organ/delete",
                            "/office/shopdetail/read", "/office/shopdetail/update", "/office/shopdetail/register", "/office/shopdetail").hasAnyRole("ADMIN", "HO", "BO");
                    // 광고 페이지 접근 권한
                    auth.requestMatchers("/advertisement/list", "/advertisement/register", "/advertisement/search/list", "/advertisement/update",
                            "/advertisement/update/read", "/advertisement/read", "/advertisement/delete").hasRole("ADMIN");
                    auth.requestMatchers("/advertisement/hitsUp").hasRole("MEMBER");

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
        http.logout(logout -> logout
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
        http.authorizeHttpRequests((auth) -> {
            auth.requestMatchers("/", "/assets/**", "/css/**", "/js/**", "/img/**", "/images/**", "/favicon.ico", "/error").permitAll();
            auth.requestMatchers("/h2-console/**").permitAll();

            // 메인 페이지
            auth.requestMatchers("/admin/").hasRole("ADMIN");
            auth.requestMatchers("/ho/**").hasAnyRole("ADMIN", "HO");
            auth.requestMatchers("/bo/**").hasAnyRole("ADMIN", "HO", "BO");
            auth.requestMatchers("/guest/**").hasAnyRole("ADMIN", "HO", "BO", "GUEST");
            auth.requestMatchers("/member/").permitAll();

            // 로그인, 회원가입, 회원정보 관련
            auth.requestMatchers("/admin/login", "/member/login", "/admin/logout", "/member/logout").permitAll();
            auth.requestMatchers("/admin/password", "/member/password", "/admin/register", "/member/register",
                    "/admin/checkEmail", "/member/checkEmail", "/admin/checkPhone", "/member/checkPhone",
                    "/admin/sendEmailCode", "/member/sendEmailCode", "/admin/checkEmailCode", "/member/checkEmailCode").permitAll();
            auth.requestMatchers("/admin/verify", "/admin/modify", "/admin/modifypw", "/admin/deleteAdmin", "/admin/deleteMember").hasAnyRole("ADMIN", "HO", "BO");
            auth.requestMatchers("/member/verify", "/member/modify", "/member/modifypw", "/member/deleteMember").hasRole("MEMBER");
            auth.requestMatchers("/member/mypage").hasRole("MEMBER");

            // 회원 목록 관련
            auth.requestMatchers("/admin/adminlist", "/admin/memberlist").hasRole("ADMIN");
            auth.requestMatchers("/admin/holist", "/admin/updateRole").hasAnyRole("ADMIN", "HO");

            // 전체
            auth.requestMatchers("/member/**").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");
            auth.requestMatchers("/admin/**").hasAnyRole("ADMIN", "HO", "BO");

            // 공지사항 페이지 접근 권한
            auth.requestMatchers("/notice/list", "/notice/read", "/notice/register", "/notice/update").hasAnyRole("ADMIN");
            auth.requestMatchers("/notice/bo/**").hasAnyRole("ADMIN", "HO");
            auth.requestMatchers("/notice/ho/**").hasAnyRole("ADMIN", "HO", "BO");
            auth.requestMatchers("/notice/userlist", "/notice/userread").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");

            // 오더 페이지 접근 권한
            auth.requestMatchers("/orders/**", "/orders/payment/**", "/payment/**").hasAnyRole("ADMIN", "HO", "BO", "MEMBER");

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

            // 메뉴, 카트 페이지 접근 권한 - RequestMapping 되어 있어서 미작성

            // 이벤트 및 포인트 페이지 접근 권한
            auth.requestMatchers("/event/**").permitAll();
            auth.requestMatchers("/event/event/**", "/event/eventread/**", "/event/eventregister/**", "/event/eventupdate", "event/memberpoint/**").hasAnyRole("ADMIN", "HO", "BO");
            auth.requestMatchers("/event/userevent/**", "/event/usereventread/**", "/event/usermemberpoint/**").hasRole("MEMBER");

            /*--------------------------------------------------------------------------------------------------------------------------*/

            // 조직-매장 페이지 접근 권한
            auth.requestMatchers("/office/list", "/office/organ", "/office/shopdetail/realread").hasRole("ADMIN");
            auth.requestMatchers("/office/ho/list", "/office/organ/ho", "/shopdetail/realread/ho").hasRole("HO");
            auth.requestMatchers("/office/bo/list", "/office/shopdetail/realread/bo").hasRole("BO");
            auth.requestMatchers("/office/member/list").hasRole("MEMBER");
            auth.requestMatchers("/office/search/list", "/office/search/hotels/list",
                    "/office/organ/register", "/office/organ/read", "/office/organ/update", "/office/organ/delete",
                    "/office/shopdetail/read", "/office/shopdetail/update", "/office/shopdetail/register", "/office/shopdetail").hasAnyRole("ADMIN", "HO", "BO");
            // 광고 페이지 접근 권한
            auth.requestMatchers("/advertisement/list", "/advertisement/register", "/advertisement/search/list", "/advertisement/update",
                    "/advertisement/update/read", "/advertisement/read", "/advertisement/delete").hasRole("ADMIN");
            auth.requestMatchers("/advertisement/hitsUp").hasRole("MEMBER");

        });

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
        http.logout(logout -> logout
                .logoutUrl("/member/logout")
                .logoutSuccessUrl("/member/login")); //로그아웃

        //일반 사용자인 경우 일반 사용자로그인 처리
        http.authenticationProvider(memberProvider());
        return http.build();
    }

}
