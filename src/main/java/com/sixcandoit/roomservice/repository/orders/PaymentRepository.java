package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.entity.orders.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,Integer> {

}
