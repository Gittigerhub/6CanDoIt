package com.sixcandoit.roomservice.entity.orders;

import com.sixcandoit.roomservice.entity.base.BaseEntity;
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
@Table(name = "payment")
public class PaymentEntity extends BaseEntity {

    @Id
    @Column(name = "payment_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                      // 기본 키

    @Column(name = "payment_price")
    private Integer paymentPrice;                 // 총 결제 금액

    @Column(name = "payment_type")
    private Integer paymentType;                  // 결제 방법(0:선결제, 1:체크 아웃 시 결제)

    @Column(name = "payment_state")
    private String paymentState;              // 결제 완료 상태(N:취소, Y:완료)

    @Column(name = "payment_card_price")
    private Integer paymentCardPrice;             // 카드 결제 금액

    @Column(name = "payment_cash_price")
    private Integer paymentCashPrice;             // 현금 결제 금액

    @Column(name = "payment_pay_type")
    private String paymentPayType;            // 결제 구분(CARD전제카드,CASH:전체현금,DIVID:분할결제)

    @Column(name = "payment_approval")
    private String paymentApproval;           // 승인 번호

    @Column(name = "order_id")
    private String orderId;                   // 주문 ID (토스페이먼츠 연동용)

    @Column(name = "error_code")
    private String errorCode;                 // 에러 코드

    @Column(name = "error_message")
    private String errorMessage;              // 에러 메시지

    @Column(name = "reg_date")
    private LocalDateTime regDate;            // 등록일시

    // 주문 테이블과 1:1 매핑
    @OneToOne(mappedBy = "paymentJoin")
    private OrdersEntity ordersJoin;

}