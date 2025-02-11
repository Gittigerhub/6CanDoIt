package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.entity.orders.OrdersMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersMenuRepository extends JpaRepository<OrdersMenuEntity, Integer> {
    //구매이력
    public List<OrdersMenuEntity> findByOrdersEntity_Idx(Integer orderIdx);
}
