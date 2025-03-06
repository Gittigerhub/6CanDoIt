package com.sixcandoit.roomservice.config;

import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// 1. UserDetails를 사용자 정의 Details로 재정의한다.
// 기존에 username, password, authority만 제공하던 내용에 호텔정보를 추가한다.
// 다음 작업은 AdminLoginService로 이동
public class CustomUserDetails implements UserDetails {

    private final AdminEntity admin;
    private final MemberEntity member;

    private final boolean isAdmin; // Admin과 Member 구분 플래그

    public CustomUserDetails(AdminEntity admin) {
        this.admin = admin;
        this.member = null;
        this.isAdmin = true;
    }

    public CustomUserDetails(MemberEntity member) {
        this.admin = null;
        this.member = member;
        this.isAdmin = false;
    }

    // Admin 엔티티의 정보 반환
    public AdminEntity getAdmin() {
        return this.admin;
    }

    // Member 엔티티의 정보 반환
    public MemberEntity getMember() {
        return this.member;
    }

//    private final String adminEmail;
//    private final String password;
//    private final String hotels;
//    private final Level level;
    private Collection<? extends GrantedAuthority> authorities;

    //public CustomUserDetails(String username, String password, String hotels, RoleType roleType) {//} Collection<? extends GrantedAuthority> authorities) {
//    public CustomUserDetails(String adminEmail, String password, Level level) {//} Collection<? extends GrantedAuthority> authorities) {
//        this.adminEmail = adminEmail;
//        this.password = password;
//        this.hotels = hotels;
//        this.level = level;
        //this.authorities = authorities;
//    }

    // 다른 UserDetails 메서드 구현...

//    public String getHotels() {
//        return this.hotels;
//    }

    // 비밀번호 오버라이딩
//    @Override
//    public String getPassword() {
//        return this.admin.getPassword();
//    }

    // 이메일 오버라이딩
//    @Override
//    public String getUsername() {
//        return this.admin.getAdminEmail();
//    }

    // 이메일 오버라이딩
    @Override
    public String getUsername() {
        return isAdmin ? admin.getAdminEmail() : member.getMemberEmail(); // Admin 또는 Member 이메일
    }

    // 비밀번호 오버라이딩
    @Override
    public String getPassword() {
        return isAdmin ? admin.getPassword() : member.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAdmin) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + this.admin.getLevel().name()));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_" + this.member.getLevel().name()));
        }
    }

    //사용자가 받은 권한을 보안인증에 맞게 형식화해서 전달
    // 권한 오버라이딩
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + admin.getLevel().name()));
//        return authorities;
//    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    // 계정 차단 여부
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    // 자격 증명 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    // 계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }


}
