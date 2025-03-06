package com.sixcandoit.roomservice.controller.payment;

import com.sixcandoit.roomservice.dto.orders.PaymentDTO;
import com.sixcandoit.roomservice.service.orders.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequiredArgsConstructor
@Log
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 목록 조회
    @GetMapping("/orders/payment/list")
    public String list(Model model) {
        log.info("결제 목록을 조회합니다.");
        List<PaymentDTO> paymentDTOList = paymentService.list();
        model.addAttribute("paymentDTOList", paymentDTOList);
        return "/orders/paymentlist";
    }

    // 결제 체크아웃 페이지 (새 결제)
    @GetMapping("/orders/payment/checkout")
    public String checkout(Model model) {
        log.info("새 결제 체크아웃 페이지를 로드합니다.");
        try {
            // 새로운 결제 객체 생성 및 기본값 설정
            PaymentDTO newPayment = new PaymentDTO();
            newPayment.setPaymentState("N"); // 결제 대기 상태
            newPayment.setPaymentPayType("CARD"); // 기본 결제 수단
            model.addAttribute("paymentDTO", newPayment);
            return "/orders/paymentcheckout";
        } catch (Exception e) {
            log.severe("결제 페이지 로드 중 오류 발생: " + e.getMessage());
            return "redirect:/orders/payment/list";
        }
    }

    // 결제 체크아웃 페이지 (기존 결제)
    @GetMapping("/orders/payment/checkout/{idx}")
    public String checkoutWithId(@PathVariable("idx") Integer idx, Model model) {
        log.info("기존 결제 체크아웃 페이지를 로드합니다. idx: " + idx);
        try {
            PaymentDTO paymentDTO = paymentService.findByIdx(idx);
            model.addAttribute("paymentDTO", paymentDTO);
            return "/orders/paymentcheckout";
        } catch (Exception e) {
            log.severe("결제 정보 조회 중 오류 발생: " + e.getMessage());
            return "redirect:/orders/payment/list";
        }
    }

    // 결제 처리
    @PostMapping("/orders/payment/process")
    @ResponseBody
    public ResponseEntity<?> processPayment(@RequestBody PaymentDTO paymentDTO) {
        log.info("결제 처리를 시작합니다.");
        try {
            PaymentDTO processedPayment = paymentService.processPayment(paymentDTO);
            return ResponseEntity.ok(processedPayment);
        } catch (Exception e) {
            log.severe("결제 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body("결제 처리 중 오류가 발생했습니다.");
        }
    }

    // 결제 취소
    @PostMapping("/orders/payment/cancel/{idx}")
    @ResponseBody
    public ResponseEntity<?> cancelPayment(@PathVariable("idx") Integer idx) {
        log.info("결제 취소를 요청합니다. idx: " + idx);
        try {
            PaymentDTO cancelledPayment = paymentService.cancelPayment(idx);
            return ResponseEntity.ok(cancelledPayment);
        } catch (Exception e) {
            log.severe("결제 취소 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body("결제 취소 중 오류가 발생했습니다.");
        }
    }

    // 결제 상세 정보 조회
    @GetMapping("/orders/payment/{idx}")
    @ResponseBody
    public ResponseEntity<?> getPaymentDetails(@PathVariable("idx") Integer idx) {
        log.info("결제 상세 정보를 조회합니다. idx: " + idx);
        try {
            PaymentDTO paymentDTO = paymentService.findByIdx(idx);
            return ResponseEntity.ok(paymentDTO);
        } catch (Exception e) {
            log.severe("결제 정보 조회 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body("결제 정보 조회 중 오류가 발생했습니다.");
        }
    }

    // 결제 검증
    @PostMapping("/orders/payment/verify")
    @ResponseBody
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentDTO paymentDTO) {
        log.info("결제 정보를 검증합니다.");
        try {
            boolean isValid = paymentService.verifyPayment(paymentDTO);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            log.severe("결제 검증 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body("결제 검증 중 오류가 발생했습니다.");
        }
    }

    // 주문별 결제 내역 조회
    @GetMapping("/orders/payment/order/{orderIdx}")
    @ResponseBody
    public ResponseEntity<?> getPaymentByOrder(@PathVariable("orderIdx") Integer orderIdx) {
        log.info("주문별 결제 내역을 조회합니다. orderIdx: " + orderIdx);
        try {
            PaymentDTO paymentDTO = paymentService.findByOrderIdx(orderIdx);
            return ResponseEntity.ok(paymentDTO);
        } catch (Exception e) {
            log.severe("주문별 결제 내역 조회 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body("주문별 결제 내역 조회 중 오류가 발생했습니다.");
        }
    }

    // 결제 수단 변경
    @PutMapping("/orders/payment/method/{idx}")
    @ResponseBody
    public ResponseEntity<?> updatePaymentMethod(
            @PathVariable("idx") Integer idx,
            @RequestBody PaymentDTO paymentDTO) {
        log.info("결제 수단을 변경합니다. idx: " + idx);
        try {
            PaymentDTO updatedPayment = paymentService.updatePaymentMethod(idx, paymentDTO);
            return ResponseEntity.ok(updatedPayment);
        } catch (Exception e) {
            log.severe("결제 수단 변경 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body("결제 수단 변경 중 오류가 발생했습니다.");
        }
    }

    // 결제 금액 변경
    @PutMapping("/orders/payment/amount/{idx}")
    @ResponseBody
    public ResponseEntity<?> updatePaymentAmount(
            @PathVariable("idx") Integer idx,
            @RequestBody PaymentDTO paymentDTO) {
        log.info("결제 금액을 변경합니다. idx: " + idx);
        try {
            PaymentDTO updatedPayment = paymentService.updatePaymentAmount(idx, paymentDTO);
            return ResponseEntity.ok(updatedPayment);
        } catch (Exception e) {
            log.severe("결제 금액 변경 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body("결제 금액 변경 중 오류가 발생했습니다.");
        }
    }

    // 결제 상태 조회
    @GetMapping("/orders/payment/status/{idx}")
    @ResponseBody
    public ResponseEntity<?> getPaymentStatus(@PathVariable("idx") Integer idx) {
        log.info("결제 상태를 조회합니다. idx: " + idx);
        try {
            String status = paymentService.getPaymentStatus(idx);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.severe("결제 상태 조회 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body("결제 상태 조회 중 오류가 발생했습니다.");
        }
    }

    // 결제 이력 조회
    @GetMapping("/orders/payment/history/{idx}")
    @ResponseBody
    public ResponseEntity<?> getPaymentHistory(@PathVariable("idx") Integer idx) {
        log.info("결제 이력을 조회합니다. idx: " + idx);
        try {
            List<PaymentDTO> history = paymentService.getPaymentHistory(idx);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.severe("결제 이력 조회 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body("결제 이력 조회 중 오류가 발생했습니다.");
        }
    }
}
