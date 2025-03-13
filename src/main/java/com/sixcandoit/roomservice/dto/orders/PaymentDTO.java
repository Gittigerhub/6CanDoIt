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
    private Integer paymentPrice;  // Total payment amount
    private Integer paymentType;   // Payment method
    private String paymentState;   // Payment status (completed, cancelled, etc.)
    private Integer paymentCardPrice;  // Card payment amount
    private Integer paymentCashPrice;  // Cash payment amount
    private String paymentPayType;   // Payment type
    private String paymentApproval;   // Approval number
    private String orderId;         // Order ID (for third-party payment gateway)
    private String errorCode;       // Error code
    private String errorMessage;    // Error message
    private LocalDateTime regDate;  // Registration date

    // Getters and Setters

    public void setPaymentDate(LocalDateTime now) {
        this.regDate = now;
    }

    public LocalDateTime getPaymentDate() {
        return regDate;
    }
}
