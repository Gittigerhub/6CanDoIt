package com.sixcandoit.roomservice.entity.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
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

    @Column(name = "cart_menu_count")
    private int cartMenuCount;             // 메뉴 수량

    // 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    @JsonBackReference
    private MemberEntity memberJoin;

    // 메뉴 테이블과 1:N 매핑
    @OneToMany(mappedBy = "cartJoin")
    private List<MenuEntity> menuJoin;

    //카트 생성 메소드
    public static CartEntity createCartEntity(MemberEntity memberJoin, int cartMenuCount) {
        if(cartMenuCount<= 0){  //메뉴 수량이 1 이상이어야 카트가 생성되도록 조건 설정
            throw new IllegalArgumentException("메뉴 수량은 1 이상이어야 합니다.");
        }

        CartEntity cart = new CartEntity();
        cart.setCartMenuCount(cartMenuCount);
        cart.setMemberJoin(memberJoin);
        return cart;
    }
}