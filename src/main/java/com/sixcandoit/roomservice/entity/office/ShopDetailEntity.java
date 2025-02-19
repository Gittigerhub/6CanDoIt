package com.sixcandoit.roomservice.entity.office;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_detail")
public class ShopDetailEntity extends BaseEntity {

    @Id
    @Column(name = "shop_detail_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                  // 기본 키

    @Column(name = "shop_detail_tel")
    private String tel;                   // 매장 연락처

    @Column(name = "shop_detail_open_time")
    private LocalTime openTime;           // 매장 오픈시간

    @Column(name = "shop_detail_close_time")
    private LocalTime closeTime;          // 매장 마감시간

    @Column(name = "shop_detail_rest_day")
    private String restDay;               // 매장 휴일

    @Column(name = "shop_detail_opne_state")
    private int openState;                // 매장 상태(0:영업중, 1종료)

    @Column(name = "shop_detail_partner_state")
    private int partnerState;             // 매장 제휴상태(0:제휴중, 1:제휴종료, 3:제휴준비중)

    @Column(name = "shop_detail_type")
    private int type;                     // 매장 타입(0:직영, 1:가맹)

    @Column(name = "bank_num")
    private String bankNum;               // 매장 계좌번호

    @Column(name = "bank_name")
    private String bankName;              // 매장 계좌 은행명

    @Column(name = "bank_owner")
    private String bankOwner;             // 매장 계좌 소유주

    @Column(name = "active_yn")
    private String activeYn;              // 매장 활성화(N:비활성화, Y:활성화)

    @Column(name = "day_fee")
    private float dayFee;                 // 매장 일별 수수료

    @Column(name = "day_fee_percent")
    private float dayFeePercent;          // 매장 일별 수수료 %

    // 조직 테이블과 1:1 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_idx")
    @JsonBackReference
    private OrganizationEntity organizationJoin;

    // 정산내역 테이블과 1:N 매칭
    @OneToMany(mappedBy = "shopDetailJoin")
    private List<CalculateEntity> calculateJoins;

    // 메뉴 테이블과 1:N 매칭
    @OneToMany(mappedBy = "shopDetailJoin")
    private List<MenuEntity> menuJoin;



}