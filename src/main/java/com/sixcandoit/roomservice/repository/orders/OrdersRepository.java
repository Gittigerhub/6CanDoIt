package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.entity.orders.OrdersEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<OrdersEntity, Integer> {

    //구매이력
    @Query("select o from OrdersEntity o where o.memberJoin.memberEmail = :memberEmail order by o.ordersDate")
    public List<OrdersEntity> findOrdersEntity(String memberEmail, Pageable pageable);
    @Query("select count(o) from OrdersEntity o where o.memberJoin.memberEmail = :memberEmail")
    public Integer totalcount(String memberEmail);

    Optional<OrdersEntity> findByIdx(Integer orderIdx);

    //public List<OrdersEntity> findByMemberEmailOrdersByOrdersDateDesc(String memberEmail, Pageable pageable);
}
