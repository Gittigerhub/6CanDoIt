package com.sixcandoit.roomservice.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PaymentDTO {

    private Integer idx;
    private Integer paymentPrice;   // 총 결제금액
    private Integer paymentType;    //결제방법
    private String paymentState; //결제완료상태
    private Integer paymentCardPrice; //카드결제금액
    private Integer paymentCashPrice; //현금결제금액
    private String paymentPayType; //결제구분
    private String paymentApproval; //승인번호




}

