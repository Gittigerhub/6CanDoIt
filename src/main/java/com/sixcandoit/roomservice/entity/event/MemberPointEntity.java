package com.sixcandoit.roomservice.entity.event;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_point")
public class MemberPointEntity extends BaseEntity {

    @Id
    @Column(name = "member_point_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                         // 기본 키

    @Column(name = "member_point")
    private int memberPoint;                     // 포인트 제목

    @Column(name = "member_point_total")
    private int memberPointTotal;                // 총 포인트

    @Column(name = "member_point_s_date")
    private LocalDateTime memberPointStartDate;  // 포인트 발급일

    @Column(name = "member_point_e_date")
    private LocalDateTime memberPointEndDate;    // 포인트 만료일

    @Column(name = "member_point_operation_yn")
    private String memberPointOperationYn;       // 포인트 사용여부(N:미사용, Y:사용)

    @Column(name = "member_point_contents")
    private String memberPointContents;          // 포인트 내용

    // 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    @JsonBackReference
    private MemberEntity memberJOIN;

}