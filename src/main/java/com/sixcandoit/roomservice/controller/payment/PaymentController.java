package com.sixcandoit.roomservice.controller.payment;

import com.sixcandoit.roomservice.dto.payment.PaymentDTO;
import com.sixcandoit.roomservice.service.Payment.PaymentService;

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

    @GetMapping("/payment/list")
    public String list(Model model){
        log.info("데이터를 읽어온다.");
        List<PaymentDTO> paymentDTOList = paymentService.list();

        model.addAttribute("paymentDTOList", paymentDTOList);
        return "payment/list";

    }
    @GetMapping("/payment/read")//localhost:8080/payment/read?idx=1
    public String read(@RequestParam("idx") Integer idx,Model model){
        log.info("읽기");
        PaymentDTO paymentDTO= paymentService.read(idx);

        log.info("read");
        model.addAttribute("paymentEntity", paymentDTO);

        return "payment/read";
    }

    }



