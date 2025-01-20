package com.sixcandoit.roomservice.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
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
}
