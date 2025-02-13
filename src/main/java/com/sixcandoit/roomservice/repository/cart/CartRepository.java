package com.sixcandoit.roomservice.repository.cart;

import com.sixcandoit.roomservice.entity.cart.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Integer> {

    //고객의 Email을 통해서 장바구니를 찾는다.
    public CartEntity findByMemberJoin_memberEmail(String memberJoin);

}
