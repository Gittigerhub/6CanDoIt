package com.sixcandoit.roomservice.controller.payment;

import com.sixcandoit.roomservice.dto.orders.PaymentDTO;
import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.entity.orders.PaymentEntity;
import com.sixcandoit.roomservice.repository.orders.PaymentRepository;
import com.sixcandoit.roomservice.service.orders.PaymentService;
import com.sixcandoit.roomservice.service.room.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class PaymentController {

    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final PaymentRepository paymentRepository;

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
            log.error("사용자 정보 처리 중 오류 발생: {}", e.getMessage());
            model.addAttribute("memberName", "게스트");
        }
    }

    // 결제 목록 조회
    @GetMapping("/orders/payment/list")
    public String list(Model model) {
        log.info("결제 목록을 조회합니다.");
        try {
            // 현재 로그인한 사용자 정보 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String memberEmail = "게스트";

            List<PaymentDTO> paymentDTOList;
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                memberEmail = auth.getName();
                // 로그인한 사용자의 결제 목록만 조회
                paymentDTOList = paymentService.listByMember(memberEmail);
            } else {
                // 비로그인 사용자는 빈 목록 반환
                paymentDTOList = List.of();
            }

            model.addAttribute("paymentDTOList", paymentDTOList);
            model.addAttribute("memberName", memberEmail);
            return "orders/paymentlist";
        } catch (Exception e) {
            log.error("결제 목록 조회 중 오류 발생: {}", e.getMessage());
            return "redirect:/orders?error=" + e.getMessage();
        }
    }

    // 결제 체크아웃 페이지
    @GetMapping("/orders/payment/checkout")
    public String checkout(@RequestParam("amount") Integer amount,
                           @RequestParam("orderId") String orderId,
                           Model model) {
        log.info("결제 체크아웃 - 금액: {}, 주문번호: {}", amount, orderId);
        try {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setPaymentPrice(amount);
            paymentDTO.setOrderId(orderId);

            model.addAttribute("paymentDTO", paymentDTO);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String memberEmail = authentication.getName();
            model.addAttribute("memberName", memberEmail);

            return "orders/paymentcheckout";
        } catch (Exception e) {
            log.error("결제 체크아웃 처리 중 오류 발생: {}", e.getMessage());
            return "redirect:/orders?error=" + e.getMessage();
        }
    }

    // 결제 체크아웃 페이지 (기존 결제)
    @GetMapping("/orders/payment/checkout/{idx}")
    public String checkoutWithId(@PathVariable("idx") Integer idx, Model model) {
        log.info("기존 결제 체크아웃 페이지 로드 - idx: {}", idx);
        try {
            PaymentDTO paymentDTO = paymentService.findByIdx(idx);
            model.addAttribute("paymentDTO", paymentDTO);
            addUserInfoToModel(model);
            return "orders/paymentcheckout";
        } catch (Exception e) {
            log.error("결제 정보 조회 중 오류 발생: {}", e.getMessage());
            return "redirect:/orders/payment/list?error=" + e.getMessage();
        }
    }

    // 결제 상세 정보 조회
    @GetMapping("/orders/payment/{idx}")
    @ResponseBody
    public ResponseEntity<?> getPaymentDetails(@PathVariable("idx") Integer idx) {
        log.info("결제 상세 정보 조회 - idx: {}", idx);
        try {
            PaymentDTO paymentDTO = paymentService.findByIdx(idx);
            return ResponseEntity.ok(paymentDTO);
        } catch (Exception e) {
            log.error("결제 정보 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("결제 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 주문별 결제 내역 조회
    @GetMapping("/orders/payment/order/{orderIdx}")
    @ResponseBody
    public ResponseEntity<?> getPaymentByOrder(@PathVariable("orderIdx") Integer orderIdx) {
        log.info("주문별 결제 내역 조회 - orderIdx: {}", orderIdx);
        try {
            PaymentDTO paymentDTO = paymentService.findByOrderIdx(orderIdx);
            return ResponseEntity.ok(paymentDTO);
        } catch (Exception e) {
            log.error("주문별 결제 내역 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("주문별 결제 내역 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 결제 상태 조회
    @GetMapping("/orders/payment/status/{idx}")
    @ResponseBody
    public ResponseEntity<?> getPaymentStatus(@PathVariable("idx") Integer idx) {
        log.info("결제 상태 조회 - idx: {}", idx);
        try {
            String status = paymentService.getPaymentStatus(idx);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("결제 상태 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("결제 상태 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 결제 성공 처리
    @GetMapping("/orders/paymentsuccess")
    public String paymentSuccess(
            @RequestParam("paymentKey") String paymentKey,
            @RequestParam("orderId") String orderId,
            @RequestParam("amount") Integer amount,
            Model model) {
        log.info("결제 성공 처리 - paymentKey: {}, orderId: {}, amount: {}", paymentKey, orderId, amount);

        try {
            // 현재 로그인한 사용자 정보 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                throw new IllegalStateException("로그인이 필요합니다.");
            }
            String memberEmail = auth.getName();

            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setPaymentPrice(amount);
            paymentDTO.setPaymentState("Y");
            paymentDTO.setPaymentPayType("CARD");
            paymentDTO.setPaymentApproval(paymentKey);
            paymentDTO.setOrderId(orderId);
            paymentDTO.setPaymentType(0);
            paymentDTO.setPaymentCardPrice(amount);
            paymentDTO.setPaymentCashPrice(0);

            PaymentDTO processedPayment = paymentService.processPayment(paymentDTO, memberEmail);

            if (orderId.startsWith("ROOM_")) {
                Integer reservationId = Integer.parseInt(orderId.substring(5));
                reservationService.updateReservationPayment(reservationId, processedPayment.getIdx());
            }

            model.addAttribute("orderId", orderId);
            model.addAttribute("paymentAmount", amount);
            model.addAttribute("approvalNo", paymentKey);
            model.addAttribute("paymentDate", processedPayment.getRegDate());
            model.addAttribute("memberName", memberEmail);

            return "orders/paymentsuccess";
        } catch (Exception e) {
            log.error("결제 성공 처리 중 오류 발생: {}", e.getMessage());
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
        log.info("결제 실패 처리 - code: {}, message: {}, orderId: {}", code, message, orderId);

        try {
            // 현재 로그인한 사용자 정보 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                throw new IllegalStateException("로그인이 필요합니다.");
            }
            String memberEmail = auth.getName();

            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setPaymentState("N");
            paymentDTO.setOrderId(orderId);
            paymentDTO.setErrorCode(code);
            paymentDTO.setErrorMessage(message);
            paymentDTO.setPaymentType(0);
            paymentDTO.setPaymentPayType("CARD");

            paymentService.processPayment(paymentDTO, memberEmail);
            model.addAttribute("error", message);
            model.addAttribute("memberName", memberEmail);
            return "orders/paymentfail";
        } catch (Exception e) {
            log.error("결제 실패 처리 중 오류 발생: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "orders/paymentfail";
        }
    }

    @GetMapping("/orders/payment/cancel/{idx}")
    public String cancel(@PathVariable("idx") Integer idx, Model model, RedirectAttributes redirectAttributes) {
        log.info("결제 취소 페이지 요청 - idx:{}", idx);
        try {
            // 현재 로그인한 사용자 정보 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                throw new IllegalStateException("로그인이 필요합니다.");
            }
            String memberEmail = auth.getName();
            
            PaymentDTO paymentDTO;
            
            // 예약 번호로 들어온 경우 (ROOM_로 시작하는 orderId를 가진 결제 찾기)
            String orderId = "ROOM_" + idx;
            PaymentEntity paymentEntity = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));
            
            // 본인의 결제인지 확인
            if (!memberEmail.equals(paymentEntity.getMemberJoin().getMemberEmail())) {
                throw new IllegalStateException("본인의 결제만 취소할 수 있습니다.");
            }
            
            paymentDTO = paymentService.convertToDTO(paymentEntity);

            if (paymentDTO == null) {
                model.addAttribute("error", "결제 정보를 찾을 수 없습니다.");
                return "redirect:/orders/payment/list";
            }

            model.addAttribute("paymentDTO", paymentDTO);
            model.addAttribute("memberName", memberEmail);
            return "orders/paymentcancel";
        } catch (Exception e) {
            log.error("결제 취소 페이지 로딩 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/payment/list";
        }
    }

    // 결제 취소 처리
    @PostMapping("/orders/payment/cancel")
    public String cancelPayment(@RequestParam("idx") Integer idx, RedirectAttributes redirectAttributes) {
        log.info("결제 취소를 처리합니다. idx: {}", idx);

        try {
            // 현재 로그인한 사용자 정보 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                throw new IllegalStateException("로그인이 필요합니다.");
            }
            String memberEmail = auth.getName();

            PaymentEntity paymentEntity = paymentRepository.findById(idx)
                    .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다. idx: " + idx));

            // 본인의 결제인지 확인
            if (!memberEmail.equals(paymentEntity.getMemberJoin().getMemberEmail())) {
                throw new IllegalStateException("본인의 결제만 취소할 수 있습니다.");
            }

            // 결제 상태 검증
            if ("N".equals(paymentEntity.getPaymentState())) {
                throw new IllegalStateException("이미 취소된 결제입니다.");
            }

            paymentEntity.setPaymentState("N"); // 결제 취소 상태로 설정
            paymentEntity.setPaymentApproval(null); // 승인번호 초기화

            // 환불 처리 (구현 필요)
            paymentService.processRefund(paymentEntity);

            // 결제 취소 정보 저장
            PaymentEntity savedEntity = paymentRepository.save(paymentEntity);

            // 예약 상태도 취소로 변경
            if (savedEntity.getOrderId() != null && savedEntity.getOrderId().startsWith("ROOM_")) {
                Integer reservationId = Integer.parseInt(savedEntity.getOrderId().substring(5));
                reservationService.updateReservationStatus(reservationId, "0");
            }

            // 취소 완료 메시지와 함께 리다이렉트
            redirectAttributes.addAttribute("success", true);
            return "redirect:/orders/paymentcancelled";
        } catch (Exception e) {
            log.error("결제 취소 처리 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/payment/list";
        }
    }
}