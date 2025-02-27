package com.sixcandoit.roomservice.controller.payment;

import com.sixcandoit.roomservice.dto.orders.PaymentDTO;
import com.sixcandoit.roomservice.service.orders.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;


@Controller
@RequiredArgsConstructor
@Log
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/orders/payment/list")
    public String list(Model model){
        log.info("데이터를 읽어온다.");
        List<PaymentDTO> paymentDTOList = paymentService.list();
        model.addAttribute("paymentDTOList", paymentDTOList);
        return "/orders/paymentlist";

    }
    @GetMapping("/orders/payment/read")
    public String readString(@RequestParam("idx") Integer idx, Model model){
        log.info("읽기");
        List<PaymentDTO> paymentDTOList = paymentService.list(); // Get all payments
        PaymentDTO paymentDTO = paymentDTOList.stream()
                .filter(payment -> payment.getIdx().equals(idx)) // Find the payment with matching idx
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 Id의 결제 정보를 찾을 수 없습니다.")); // Handle error if not found

        log.info("read");
        model.addAttribute("paymentDTO", paymentDTO);

        return "/orders/paymentread";
    }

}