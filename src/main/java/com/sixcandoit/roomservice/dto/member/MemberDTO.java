package com.sixcandoit.roomservice.dto.member;

import com.sixcandoit.roomservice.constant.Level;
import lombok.*;

@Getter
@Setter
@ToString
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

    private Level level;    //사용자 등급(열거값)
    private String membersDescription;  //열거형 설명을 저장할 변수

    //해당키와 설명을 저장하는 사용자 함수를 선언
    public void setmembers(Level level) {
        this.level = level; //키 값 저장
        this.membersDescription =
                level != null? level.getDescription():null;
    }
}
