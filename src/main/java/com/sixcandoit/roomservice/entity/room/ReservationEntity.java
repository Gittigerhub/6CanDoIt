package com.sixcandoit.roomservice.entity.room;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
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

    @Column(name = "res_status")
    private String resStatus;          // 예약 상태(1:취소, 2:예약, 3:체크인, 4:체크 아웃)

    @Column(name = "startdate")
    private LocalDate startDate;       // 예약 시작 일자

    @Column(name = "enddate")
    private LocalDate endDate;         // 예약 마지막 일자

    // 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    @JsonBackReference
    private MemberEntity memberJoin;

}