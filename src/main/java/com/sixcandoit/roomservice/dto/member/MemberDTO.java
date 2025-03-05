package com.sixcandoit.roomservice.dto.member;

import com.sixcandoit.roomservice.constant.Level;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class MemberDTO {
    private Integer idx;             // 기본 키
    private String memberType;             // 회원 가입타입
    private String memberEmail;            // 이메일
    private String password;              // 비밀번호
    private String memberBirth;            // 생년월일
    private String memberGender;           // 성별
    private String memberName;             // 이름
    private String memberPhone;            // 연락처

//    private Integer memberPostcode;            // 우편번호
    private String memberAddress;          // 주소
//    private String memberExtraAddress;     // 상세 주소

    private String alramOrder;             // 주문 알람 수신여부
//    private String activeYn;               // 활성화 유무
    private LocalDateTime insDate;                 // 가입 일자


    private Level level;    //사용자 등급(열거값)
    private String membersDescription;  //열거형 설명을 저장할 변수

    //해당키와 설명을 저장하는 사용자 함수를 선언
    public void setmembers(Level level) {
        this.level = level; //키 값 저장
        this.membersDescription =
                level != null? level.getDescription():null;
    }
//
//    //UserDetails를 사용자 커스텀으로 변경
//
//    //비밀번호 오버라이딩
//    @Override
//    public String getPassword() {
//        return "memberPwd";
//    }
//    //이메일 오버라이딩
//    @Override
//    public String getUsername() {
//        return "memberEmail";
//    }
//    //권한 오버라이딩
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        if(level != null){
//            switch (level){
//                case MEMBER:
//                    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"));
//            }
//        }
//        return Collections.emptyList();
//    }
//
//
//    //계정 만료 여부
//    @Override
//    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
//    }
//    //계정 차단 여부
//    @Override
//    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
//    }
//    //자격 증명 여부
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
//    }
//    //계정 활성화 여부
//    @Override
//    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
//    }
}
