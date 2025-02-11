package com.sixcandoit.roomservice.entity.orders;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.constant.OrderStatus;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "orders_status")
    private OrderStatus ordersStatus;             // 주문 상태(NEW 신규, CHECK 접수, COOKING 조리 중, CANCEL 취소, CLOSE 완료)

    @Column(name = "orders_phone")
    private String ordersPhone;              // 연락받을 연락처

    @Column(name = "orders_memo")
    private String ordersMemo;               // 주문 요청 사항

    // 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    @JsonBackReference
    private MemberEntity memberJoin;

    // 결제 내역 테이블과 1:1 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_idx")
    @JsonBackReference
    private PaymentEntity paymentJoin;

    // 주문 상품과 1:N 매핑
    // mappedBy = "orders" -> ordersMenu에 있는 orders에 의해 관리
    @OneToMany(mappedBy = "ordersEntity", cascade = CascadeType.ALL,
    orphanRemoval = true, fetch = FetchType.LAZY)   //양방향 설정
    private List<OrdersMenuEntity> ordersMenuEntityList = new ArrayList<>();

    //주문일
    private LocalDateTime ordersDate;


    public void setOrdersMenuEntityList(OrdersMenuEntity ordersMenuEntity) {
        this.ordersMenuEntityList.add(ordersMenuEntity);
    }

    public void setOrdersMenuEntityList(List<OrdersMenuEntity> ordersMenuEntityList) {
        this.ordersMenuEntityList = ordersMenuEntityList;
    }
}