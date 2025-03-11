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
    private Integer idx;                // 기본값

    @Column(name = "orders_menu_count")
    private int count;                  // 수량

    // 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_idx")
    private OrdersEntity ordersJoin;

    // 메뉴 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_idx")
    private MenuEntity menuJoin;

}