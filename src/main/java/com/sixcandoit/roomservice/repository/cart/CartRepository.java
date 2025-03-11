package com.sixcandoit.roomservice.repository.cart;

import com.sixcandoit.roomservice.entity.cart.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Integer> {

    //고객의 Email을 통해서 장바구니를 찾는다.
    @Query("select c from CartEntity c where c.memberJoin.idx = :idx")
    Optional<CartEntity> findByMemberJoin(@Param("idx") Integer idx);

}