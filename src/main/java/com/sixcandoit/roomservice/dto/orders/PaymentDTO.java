package com.sixcandoit.roomservice.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PaymentDTO {

    private Integer idx;
    private Integer paymentPrice;   // 총 결제금액
    private Integer paymentType;    // 결제방법
    private String paymentState;    // 결제완료상태
    private Integer paymentCardPrice; // 카드결제금액
    private Integer paymentCashPrice; // 현금결제금액
    private String paymentPayType;  // 결제구분
    private String paymentApproval; // 승인번호
    private String orderId;         // 주문 ID (토스페이먼츠 연동용)
    private String errorCode;       // 에러 코드
    private String errorMessage;    // 에러 메시지
    private LocalDateTime regDate;  // 등록일시

    public PaymentDTO(Integer idx, Integer paymentPrice, String paymentState, Integer paymentCardPrice, String paymentPayType, String paymentApproval) {
        this.idx = idx;
        this.paymentPrice = paymentPrice;
        this.paymentState = paymentState;
        this.paymentCardPrice = paymentCardPrice;
        this.paymentPayType = paymentPayType;
        this.paymentApproval = paymentApproval;
    }
}
