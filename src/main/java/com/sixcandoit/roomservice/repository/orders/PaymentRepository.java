package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.entity.orders.PaymentEntity;
import com.sixcandoit.roomservice.entity.orders.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {
    List<PaymentEntity> findByOrdersJoin(OrdersEntity ordersEntity);
    List<PaymentEntity> findByOrdersJoinAndPaymentPayType(OrdersEntity ordersEntity, String paymentPayType);
    List<PaymentEntity> findByPaymentState(String paymentState);
    Optional<PaymentEntity> findByOrderId(String orderId);
    List<PaymentEntity> findByMemberJoin_MemberEmail(String memberEmail);

    // 회원의 예약 결제 내역 조회 (JPQL 사용)
    @Query("SELECT p FROM PaymentEntity p JOIN ReservationEntity r ON p.orderId = CONCAT('ROOM_', r.idx) WHERE r.memberJoin.memberEmail = :memberEmail")
    List<PaymentEntity> findByReservationMemberEmail(@Param("memberEmail") String memberEmail);
}