package com.sixcandoit.roomservice.entity.orders;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_menu")
public class CartMenuEntity {

    @Id
    @Column(name = "cart_menu_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;    //기본 키

    // 카트 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_idx")
    @JsonBackReference
    private CartEntity cartEntity;

    //메뉴 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_idx")
    @JsonBackReference
    private MenuEntity menuEntity;

    //장바구니에 담긴 수량
    private int count;

    public static CartMenuEntity createCartMenuEntity(CartEntity cartEntity, MenuEntity menuEntity, int count) {
        CartMenuEntity cartMenuEntity = new CartMenuEntity();
        cartMenuEntity.setCartEntity(cartEntity);
        cartMenuEntity.setMenuEntity(menuEntity);
        cartMenuEntity.setCount(count);

        return cartMenuEntity;
    }

    //수량 증가
    public void addCount(int count) {
        this.count += count;
    }
}
