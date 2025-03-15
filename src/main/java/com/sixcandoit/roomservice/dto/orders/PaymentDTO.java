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


    // Setters for paymentState and paymentType with validation
    public void setPaymentState(String paymentState) {
        if (!paymentState.equals("Y") && !paymentState.equals("N")) {
            throw new IllegalArgumentException("Invalid payment state: " + paymentState);
        }
        this.paymentState = paymentState;
    }

    public void setPaymentType(Integer paymentType) {
        if (paymentType != 0 && paymentType != 1) {
            throw new IllegalArgumentException("Invalid payment type: " + paymentType);
        }
        this.paymentType = paymentType;
    }

    // Utility method to set error information
    public void setError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getReservationIdx() {
        return null;
    }

    public void setMemberEmail(String memberEmail) {
    }
}