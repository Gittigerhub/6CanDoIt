package com.sixcandoit.roomservice.entity.room;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.orders.PaymentEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "res")
public class ReservationEntity extends BaseEntity {

    @Id
    @Column(name = "res_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                    // 기본 키

    @Column(name="username", length = 20) //예약자
    private String username;

    @Column(name = "startdate")
    private LocalDate startDate;       // 예약 시작 일자

    @Column(name = "enddate")
    private LocalDate endDate;         // 예약 마지막 일자

    @Column(name = "res_status")
    private String resStatus;          // 예약 상태(1:예약중, 2:예약완료, 3:예약종료)

    // 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    @JsonBackReference
    private MemberEntity memberJoin;

    // 룸 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_idx")
    @JsonBackReference
    private RoomEntity roomJoin;

    // 결제 테이블과 1:1 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_idx")
    @JsonBackReference
    private PaymentEntity paymentJoin;
}