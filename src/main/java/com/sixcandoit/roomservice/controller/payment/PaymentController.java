package com.sixcandoit.roomservice.controller.payment;

import com.sixcandoit.roomservice.dto.orders.PaymentDTO;
import com.sixcandoit.roomservice.service.orders.PaymentService;
import com.sixcandoit.roomservice.service.room.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log

public class PaymentController {

    private final PaymentService paymentService;
    private final ReservationService reservationService;

    // 사용자 정보를 모델에 추가하는 메서드
    private void addUserInfoToModel(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String memberName = "게스트";

            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                Object principal = auth.getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    memberName = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                } else {
                    memberName = auth.getName();
                }
            }

            model.addAttribute("memberName", memberName);
        } catch (Exception e) {
            log.severe("사용자 정보 처리 중 오류 발생: " + e.getMessage());
            model.addAttribute("memberName", "게스트");
        }
    }

    // 결제 목록 조회
    @GetMapping("/orders/payment/list")
    public String list(Model model) {
        log.info("결제 목록을 조회합니다.");
        List<PaymentDTO> paymentDTOList = paymentService.list();
        model.addAttribute("paymentDTOList", paymentDTOList);
        addUserInfoToModel(model);
        return "orders/paymentlist";
    }

    // 결제 체크아웃 페이지
    @GetMapping("/orders/payment/checkout")
    public String checkout(Model model) {
        log.info("결제 체크아웃 페이지를 로드합니다.");
        try {
            PaymentDTO newPayment = new PaymentDTO();
            // 테스트용 데이터 설정
            newPayment.setPaymentPrice(1000);
            newPayment.setOrderId("ORDER_" + System.currentTimeMillis());
            
            model.addAttribute("paymentDTO", newPayment);
            addUserInfoToModel(model);
            return "orders/paymentcheckout";
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
            addUserInfoToModel(model);
            return "orders/paymentcheckout";
        } catch (Exception e) {
            log.severe("결제 정보 조회 중 오류 발생: " + e.getMessage());
            return "redirect:/orders/payment/list";
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

    // 결제 성공 처리
    @GetMapping("/orders/paymentsuccess")
    public String paymentSuccess(
            @RequestParam("paymentKey") String paymentKey,
            @RequestParam("orderId") String orderId,
            @RequestParam("amount") Integer amount,
            Model model) {
        log.info("결제 성공 처리를 시작합니다. paymentKey: " + paymentKey + ", orderId: " + orderId + ", amount: " + amount);

        try {
            // 결제 정보 처리
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setPaymentPrice(amount);
            paymentDTO.setPaymentState("Y");
            paymentDTO.setPaymentPayType("CARD");
            paymentDTO.setPaymentApproval(paymentKey);
            paymentDTO.setOrderId(orderId);
            paymentDTO.setPaymentType(0);
            paymentDTO.setPaymentCardPrice(amount);
            paymentDTO.setPaymentCashPrice(0);

            PaymentDTO processedPayment = paymentService.processPayment(paymentDTO);

            // 예약 정보 업데이트
            if (orderId.startsWith("ROOM_")) {
                Integer reservationId = Integer.parseInt(orderId.substring(5));
                reservationService.updateReservationPayment(reservationId, processedPayment.getIdx());
            }

            // 결제 성공 페이지로 이동하면서 필요한 정보 전달
            model.addAttribute("orderId", orderId);
            model.addAttribute("paymentAmount", amount);
            model.addAttribute("approvalNo", paymentKey);
            model.addAttribute("paymentDate", processedPayment.getRegDate());
            addUserInfoToModel(model);
            
            return "orders/paymentsuccess";
        } catch (Exception e) {
            log.severe("결제 성공 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/orders/paymentfail";
        }
    }


    // 결제 실패 처리
    @GetMapping("/orders/payment/fail")
    public String paymentFail(
            @RequestParam("code") String code,
            @RequestParam("message") String message,
            @RequestParam("orderId") String orderId,
            Model model) {
        log.info("결제 실패를 처리합니다. code: " + code + ", message: " + message + ", orderId: " + orderId);

        try {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setPaymentState("N");
            paymentDTO.setOrderId(orderId);
            paymentDTO.setErrorCode(code);
            paymentDTO.setErrorMessage(message);
            paymentDTO.setPaymentType(0);
            paymentDTO.setPaymentPayType("CARD");

            PaymentDTO failedPayment = paymentService.processPayment(paymentDTO);
            addUserInfoToModel(model);
            return "redirect:/orders/payment/list?error=" + message;
        } catch (Exception e) {
            log.severe("결제 실패 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            addUserInfoToModel(model);
            return "redirect:/orders/payment/list?error=" + e.getMessage();
        }
    }

    // 결제 상세 정보 페이지
    @GetMapping("/orders/payment/detail/{idx}")
    public String detail(@PathVariable("idx") Integer idx, Model model) {
        log.info("결제 상세 정보 페이지를 로드합니다. idx: " + idx);
        try {
            PaymentDTO paymentDTO = paymentService.findByIdx(idx);
            model.addAttribute("payment", paymentDTO);
            addUserInfoToModel(model);
            return "orders/paymentdetail";
        } catch (Exception e) {
            log.severe("결제 정보 조회 중 오류 발생: " + e.getMessage());
            return "redirect:/orders/payment/list?error=" + e.getMessage();
        }
    }
}