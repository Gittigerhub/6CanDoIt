package com.sixcandoit.roomservice.dto.member;

import com.sixcandoit.roomservice.constant.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO implements UserDetails {
    private Integer idx;             // 기본 키
    private String memberType;             // 회원 가입타입
    private String memberEmail;            // 이메일
    private String memberPwd;              // 비밀번호
    private String memberBirth;            // 생년월일
    private String memberGender;           // 성별
    private String memberName;             // 이름
    private String memberPhone;            // 연락처
    private String memberAddress;          // 주소
    private String alramOrder;             // 주문 알람 수신여부
    private String activeYn;               // 활성화 유무

    private Level level;
    private String membersDescription;
    public void setmembers(Level level) {
        this.level = level;
        this.membersDescription =
                level != null? level.getDescription():null;
    }

    //사용자 이름을 출력하는 메소드
    public String getDisplayMemberName(){
        return memberName;
    }

    @Override
    public String getPassword() {
        return "memberPwd";
    }

    @Override
    public String getUsername() {
        return "memberEmail";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(level != null){
            switch (level){
                case MEMBER:
                    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"));
            }
        }
        return Collections.emptyList();
    }



    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
