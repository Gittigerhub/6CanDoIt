package com.sixcandoit.roomservice.entity.orders;

import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.office.ShopDetailEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "menu")
public class MenuEntity extends BaseEntity {

    @Id
    @Column(name = "menu_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                 // 기본 키

    @Column(name = "menu_name")
    private String menuName;             // 메뉴 이름

    @Column(name = "menu_content")
    private String menuContent;          // 메뉴 설명

    @Column(name = "menu_price")
    private int menuPrice;               // 메뉴 가격

    @Column(name = "menu_img")
    private String menuImg;              // 메뉴 이미지 경로

    @Column(name = "menu_option_yn")
    private String menuOptionYn;         // 옵션 존재여부(N:없음, Y:있음)

    @Column(name = "active_yn")
    private String activeYn;             // 활성화유무(N:비활성, Y: 활성)

    @Column(name = "menu_sales_yn")
    private String menuSalesYn;          // 세일 여부(N:안함, Y:세일중)

    @Column(name = "menu_sale_type")
    private String menuSaleType;         // 할인구분(NONE:없음,AMOUNT:할인액,PERSENT:항인율)

    @Column(name = "menu_sale_amount")
    private int menuSaleAmount;          // 할인액

    @Column(name = "menu_sale_percent")
    private int menuSalePercent;         // 할인율

    // 매장 상세 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_detail_idx")
    private ShopDetailEntity shopDetailEntity;

    // 메뉴 옵션 테이블과 1:N 매핑
    @OneToMany(mappedBy = "menuEntity")
    private List<MenuOptionEntity> menuOptionEntities;

    // 장바구니 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_idx")
    private CartEntity cartEntity;

}