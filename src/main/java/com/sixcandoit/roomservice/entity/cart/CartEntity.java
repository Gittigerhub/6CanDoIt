package com.sixcandoit.roomservice.entity.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.room.ReservationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class CartEntity extends BaseEntity {

    @Id
    @Column(name = "cart_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                   // 기본 키

    // 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    @JsonBackReference
    private MemberEntity memberJoin;

    // 카트메뉴 테이블과 1:N 매핑
    @OneToMany(mappedBy = "cartEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartMenuEntity> cartMenuJoin;

    // 카트메뉴 생성과 동시에 FK 자동추가
    public void addCartMenu(CartMenuEntity cartMenu) {
        this.cartMenuJoin.add(cartMenu);
        cartMenu.setCartEntity(this);
    }

    // 예약 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "res_idx")
    @JsonBackReference
    private ReservationEntity reservationJoin;

    // 카트 생성 메소드
    public static CartEntity createCartEntity(MemberEntity memberJoin, ReservationEntity reservationJoin) {

        CartEntity cart = new CartEntity();
        cart.setMemberJoin(memberJoin);
        cart.setReservationJoin(reservationJoin);
        return cart;

    }

}