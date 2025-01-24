package com.sixcandoit.roomservice.entity.orders;

import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrdersEntity extends BaseEntity {

    @Id
    @Column(name = "orders_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                     // 기본 키

    @Column(name = "orders_payment_type")
    private int ordersPaymentType;           // 결제 타입(B:선 결제, A:체크아웃 시 결제)

    @Column(name = "orders_status")
    private String ordersStatus;             // 주문 상태(NEW 신규, CHECK 접수, COOKING 조리 중, CANCEL 취소, CLOSE 완료)

    @Column(name = "orders_phone")
    private String ordersPhone;              // 연락받을 연락처

    @Column(name = "orders_memo")
    private String ordersMemo;               // 주문 요청 사항

    // 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private MemberEntity memberJoin;

    // 결제 내역 테이블과 1:1 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_idx")
    private PaymentEntity paymentJoin;

}