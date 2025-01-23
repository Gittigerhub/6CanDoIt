package com.sixcandoit.roomservice.entity.member;

import com.sixcandoit.roomservice.constant.Level;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.orders.CartEntity;
import com.sixcandoit.roomservice.entity.orders.OrdersEntity;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import com.sixcandoit.roomservice.entity.room.ReservationEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class MemberEntity extends BaseEntity {

    @Id
    @Column(name = "member_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;             // 기본 키

    @Column(name = "member_type")
    private String memberType;             // 회원 가입타입

    @Column(name = "member_email")
    private String memberEmail;            // 이메일

    @Column(name = "member_pwd")
    private String memberPwd;              // 비밀번호

    @Column(name = "member_birth")
    private String memberBirth;            // 생년월일

    @Column(name = "member_gender")
    private String memberGender;           // 성별

    @Column(name = "member_name")
    private String memberName;             // 이름

    @Column(name = "member_phone")
    private String memberPhone;            // 연락처

    @Column(name = "member_address")
    private String memberAddress;          // 주소

    @Column(name = "alram_order")
    private String alramOrder;             // 주문 알람 수신여부

    @Column(name = "active_yn")
    private String activeYn;               // 활성화 유무

    @Enumerated(EnumType.STRING)
    private Level level;                  // 유저 권한

    // 예약 테이블과 1:N 매핑
    @OneToMany(mappedBy = "memberEntity")
    private List<ReservationEntity> reservationEntities = new ArrayList<ReservationEntity>();

    // 문의 사항 테이블과 1:N 매핑
    @OneToMany(mappedBy = "memberEntity")
    private List<QnaEntity> qnaEntities = new ArrayList<QnaEntity>();

    // 회원 포인트 테이블과 1:N 매핑
    @OneToMany(mappedBy = "memberEntity")
    private List<MemberPointEntity> memberPointEntities = new ArrayList<MemberPointEntity>();

    // 장바구니 테이블과 1:N 매핑
    @OneToMany(mappedBy = "memberEntity")
    private List<CartEntity> cartEntities = new ArrayList<CartEntity>();

    // 주문 테이블과 1:N 매핑
    @OneToMany(mappedBy = "memberEntity")
    private List<OrdersEntity> ordersEntities = new ArrayList<OrdersEntity>();

}