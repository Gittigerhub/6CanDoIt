package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.entity.orders.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Integer> {

    //고객의 Email을 통해서 장바구니를 찾는다.
    public CartEntity findByMemberJoin_memberEmail(String memberJoin);

}
