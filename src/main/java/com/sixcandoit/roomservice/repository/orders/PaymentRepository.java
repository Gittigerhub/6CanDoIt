package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.entity.orders.OrdersEntity;
import com.sixcandoit.roomservice.entity.orders.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,Integer> {

    List<PaymentEntity> findByOrdersJoinAndPaymentPayType(OrdersEntity order, String divid);
}
