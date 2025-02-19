package com.sixcandoit.roomservice.controller.payment;

import com.sixcandoit.roomservice.entity.orders.PaymentEntity;
import com.sixcandoit.roomservice.service.payment.PaymentService;


import lombok.RequiredArgsConstructor;

import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
@Log
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/payment")
    public String showPaymentPage(Model model) {
        model.addAttribute("payment", new PaymentEntity());
        return "payment";

    }
}



