package com.sixcandoit.roomservice.service.orders;

import com.sixcandoit.roomservice.dto.orders.PaymentDTO;
import com.sixcandoit.roomservice.entity.orders.PaymentEntity;
import com.sixcandoit.roomservice.entity.orders.OrdersEntity;
import com.sixcandoit.roomservice.repository.orders.PaymentRepository;
import com.sixcandoit.roomservice.repository.orders.OrdersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrdersRepository ordersRepository;

    // 결제 목록 조회
    public List<PaymentDTO> list() {
        log.info("결제 목록을 조회합니다.");
        List<PaymentEntity> paymentEntities = paymentRepository.findAll();
        return paymentEntities.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // 특정 결제 정보 조회
    public PaymentDTO findByIdx(Integer idx) {
        log.info("결제 정보를 조회합니다. idx: " + idx);
        PaymentEntity paymentEntity = paymentRepository.findById(idx)
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다. idx: " + idx));
        return convertToDTO(paymentEntity);
    }

    // 결제 처리
    public PaymentDTO processPayment(PaymentDTO paymentDTO) {
        log.info("결제 처리를 시작합니다.");

        // 결제 검증
        if (!verifyPayment(paymentDTO)) {
            throw new IllegalStateException("결제 정보가 유효하지 않습니다.");
        }

        // 결제 금액 검증
        validatePaymentAmount(paymentDTO);

        // 결제 엔티티 생성 후 저장
        PaymentEntity paymentEntity = convertToEntity(paymentDTO);
        paymentEntity.setPaymentState("Y");  // 결제 완료 상태
        paymentEntity.setPaymentApproval(generateApprovalNumber()); // 승인번호 생성

        PaymentEntity savedEntity = paymentRepository.save(paymentEntity);
        return convertToDTO(savedEntity);
    }

    // 결제 취소
    public PaymentDTO cancelPayment(Integer idx, String payment) {
        log.info("결제 취소를 처리합니다. idx: " + idx);
        PaymentEntity paymentEntity = paymentRepository.findById(idx)
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다. idx: " + idx));

        // 결제 상태 검증
        if ("Y".equals(paymentEntity.getPaymentState())) {
            throw new IllegalStateException("이미 결제 완료된 결제는 취소할 수 없습니다.");
        }

        paymentEntity.setPaymentState("N"); // 결제 취소 상태로 설정
        paymentEntity.setPaymentApproval(null); // 승인번호 초기화

        // 환불 처리 (구현 필요)
        processRefund(paymentEntity);

        PaymentEntity savedEntity = paymentRepository.save(paymentEntity);
        return convertToDTO(savedEntity);
    }

    // 결제 검증
    public boolean verifyPayment(PaymentDTO paymentDTO) {
        log.info("결제 정보를 검증합니다.");

        if (paymentDTO.getPaymentPrice() <= 0) {
            return false;
        }

        // 토스페이먼츠 결제인 경우
        if (paymentDTO.getOrderId() != null) {
            if (paymentDTO.getPaymentState() == null || !"CARD".equals(paymentDTO.getPaymentPayType())) {
                return false;
            }
            return true;
        }

        // 일반 결제인 경우
        return verifyGeneralPayment(paymentDTO);
    }

    private boolean verifyGeneralPayment(PaymentDTO paymentDTO) {
        if (paymentDTO.getPaymentType() == null || (paymentDTO.getPaymentType() != 0 && paymentDTO.getPaymentType() != 1)) {
            return false;
        }
        if (paymentDTO.getPaymentPayType() == null ||
                (!"CARD".equals(paymentDTO.getPaymentPayType()) && !"CASH".equals(paymentDTO.getPaymentPayType()) && !"DIVID".equals(paymentDTO.getPaymentPayType()))) {
            return false;
        }
        return true;
    }

    // 주문별 결제 내역 조회
    public PaymentDTO findByOrderIdx(Integer orderIdx) {
        log.info("주문별 결제 내역을 조회합니다. orderIdx: " + orderIdx);
        OrdersEntity order = ordersRepository.findById(orderIdx)
                .orElseThrow(() -> new EntityNotFoundException("주문 정보를 찾을 수 없습니다. orderIdx: " + orderIdx));

        PaymentEntity payment = order.getPaymentJoin();
        if (payment == null) {
            throw new EntityNotFoundException("해당 주문의 결제 정보를 찾을 수 없습니다.");
        }

        return convertToDTO(payment);
    }

    // 결제 수단 변경
    public PaymentDTO updatePaymentMethod(Integer idx, PaymentDTO paymentDTO) {
        log.info("결제 수단을 변경합니다. idx: " + idx);
        PaymentEntity paymentEntity = paymentRepository.findById(idx)
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다. idx: " + idx));

        // 결제 상태가 완료인 경우 수단 변경 불가
        if ("Y".equals(paymentEntity.getPaymentState())) {
            throw new IllegalStateException("이미 완료된 결제는 수단을 변경할 수 없습니다.");
        }

        paymentEntity.setPaymentType(paymentDTO.getPaymentType());
        paymentEntity.setPaymentPayType(paymentDTO.getPaymentPayType());

        PaymentEntity savedEntity = paymentRepository.save(paymentEntity);
        return convertToDTO(savedEntity);
    }

    // 결제 금액 변경
    public PaymentDTO updatePaymentAmount(Integer idx, PaymentDTO paymentDTO) {
        log.info("결제 금액을 변경합니다. idx: " + idx);
        PaymentEntity paymentEntity = paymentRepository.findById(idx)
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다. idx: " + idx));

        // 결제 상태가 완료인 경우 금액 변경 불가
        if ("Y".equals(paymentEntity.getPaymentState())) {
            throw new IllegalStateException("이미 완료된 결제는 금액을 변경할 수 없습니다.");
        }

        // 결제 금액 검증
        validatePaymentAmount(paymentDTO);

        paymentEntity.setPaymentPrice(paymentDTO.getPaymentPrice());
        paymentEntity.setPaymentCardPrice(paymentDTO.getPaymentCardPrice());
        paymentEntity.setPaymentCashPrice(paymentDTO.getPaymentCashPrice());

        PaymentEntity savedEntity = paymentRepository.save(paymentEntity);
        return convertToDTO(savedEntity);
    }

    // 결제 금액 검증
    private void validatePaymentAmount(PaymentDTO paymentDTO) {
        if (paymentDTO.getPaymentPrice() <= 0) {
            throw new IllegalStateException("결제 금액은 0보다 커야 합니다.");
        }

        if ("CARD".equals(paymentDTO.getPaymentPayType()) &&
                (paymentDTO.getPaymentCardPrice() == null || paymentDTO.getPaymentCardPrice() <= 0)) {
            throw new IllegalStateException("카드 결제 금액이 유효하지 않습니다.");
        }

        if ("CASH".equals(paymentDTO.getPaymentPayType()) &&
                (paymentDTO.getPaymentCashPrice() == null || paymentDTO.getPaymentCashPrice() <= 0)) {
            throw new IllegalStateException("현금 결제 금액이 유효하지 않습니다.");
        }

        if ("DIVID".equals(paymentDTO.getPaymentPayType())) {
            int totalDividedAmount = (paymentDTO.getPaymentCardPrice() != null ? paymentDTO.getPaymentCardPrice() : 0) +
                    (paymentDTO.getPaymentCashPrice() != null ? paymentDTO.getPaymentCashPrice() : 0);
            if (totalDividedAmount != paymentDTO.getPaymentPrice()) {
                throw new IllegalStateException("분할 결제 금액의 합이 총 결제 금액과 일치하지 않습니다.");
            }
        }
    }

    // 환불 처리 (구현 필요)
    public void processRefund(PaymentEntity paymentEntity) {
        // TODO: 실제 환불 처리 로직 구현
        log.info("환불 처리를 시작합니다. paymentIdx: " + paymentEntity.getIdx());
    }

    // 승인번호 생성
    private String generateApprovalNumber() {
        return "APP" + System.currentTimeMillis() +
                String.format("%04d", (int)(Math.random() * 10000));
    }

    // Entity를 DTO로 변환
    private PaymentDTO convertToDTO(PaymentEntity entity) {
        PaymentDTO dto = new PaymentDTO();
        dto.setIdx(entity.getIdx());
        dto.setPaymentPrice(entity.getPaymentPrice());
        dto.setPaymentType(entity.getPaymentType());
        dto.setPaymentState(entity.getPaymentState());
        dto.setPaymentCardPrice(entity.getPaymentCardPrice());
        dto.setPaymentCashPrice(entity.getPaymentCashPrice());
        dto.setPaymentPayType(entity.getPaymentPayType());
        dto.setPaymentApproval(entity.getPaymentApproval());
        dto.setOrderId(entity.getOrderId());
        dto.setErrorCode(entity.getErrorCode());
        dto.setErrorMessage(entity.getErrorMessage());
        dto.setRegDate(entity.getRegDate());
        return dto;
    }

    // DTO를 Entity로 변환
    private PaymentEntity convertToEntity(PaymentDTO dto) {
        PaymentEntity entity = new PaymentEntity();
        entity.setPaymentPrice(dto.getPaymentPrice());
        entity.setPaymentType(dto.getPaymentType());
        entity.setPaymentState(dto.getPaymentState());
        entity.setPaymentCardPrice(dto.getPaymentCardPrice());
        entity.setPaymentCashPrice(dto.getPaymentCashPrice());
        entity.setPaymentPayType(dto.getPaymentPayType());
        entity.setPaymentApproval(dto.getPaymentApproval());
        entity.setOrderId(dto.getOrderId());
        entity.setErrorCode(dto.getErrorCode());
        entity.setErrorMessage(dto.getErrorMessage());

        if (dto.getRegDate() == null) {
            entity.setRegDate(LocalDateTime.now());
        } else {
            entity.setRegDate(dto.getRegDate());
        }

        return entity;
    }

    public String getPaymentStatus(Integer idx) {
        return "";

    }
}