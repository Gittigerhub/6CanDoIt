package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.entity.orders.PaymentEntity;
import com.sixcandoit.roomservice.entity.orders.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {
    List<PaymentEntity> findByOrdersJoin(OrdersEntity ordersEntity);
    List<PaymentEntity> findByOrdersJoinAndPaymentPayType(OrdersEntity ordersEntity, String paymentPayType);
    List<PaymentEntity> findByPaymentState(String paymentState);
}
