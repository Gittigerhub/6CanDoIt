package com.sixcandoit.roomservice.entity.orders;

import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
//@ToString(exclude = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders_menu")

public class OrdersMenuEntity extends BaseEntity {

    @Id
    @Column(name = "orders_menu_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    // 회원 테이블과 1:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private OrdersEntity ordersEntity;

    // 회원 테이블과 1:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_idx")
    private MenuEntity menuEntity;

    //주문 가격
    private int orderPrice;

    //수량
    private int count;
}
