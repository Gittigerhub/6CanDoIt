package com.sixcandoit.roomservice.entity.menu;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.constant.MenuCategory;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.cart.CartEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.entity.office.ShopDetailEntity;
import com.sixcandoit.roomservice.entity.orders.OrdersMenuEntity;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_category")
    private MenuCategory menuCategory;        // 메뉴 카테고리(KF:한식, CF:중식, WF:양식, JF:일식, Dr:음료)

    @Column(name = "menu_name")
    private String menuName;             // 메뉴 이름

    @Column(name = "menu_content")
    private String menuContent;          // 메뉴 설명

    @Column(name = "menu_price")
    private int menuPrice;               // 메뉴 가격

    @Column(name = "menu_option_yn")
    private String menuOptionYn;         // 옵션 존재여부(N:없음, Y:있음)

    @Column(name = "active_yn")
    private String activeYn;             // 활성화유무(N:비활성, Y: 활성)

    @Column(name = "menu_sales_yn")
    private String menuSalesYn;          // 세일 여부(N:안함, Y:세일중)

    @Column(name = "menu_sale_type")
    private String menuSaleType;         // 할인구분(NONE:없음,AMOUNT:할인액,PERSENT:할인율)

    @Column(name = "origin_Price")
    private int originPrice;            // 원가

    @Column(name = "menu_sale_amount")
    private int menuSaleAmount;          // 할인액

    @Column(name = "menu_sale_percent")
    private int menuSalePercent;         // 할인율

    // 조직 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_idx")
    @JsonBackReference
    private OrganizationEntity organizationJoin;

    // 매장 상세 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_detail_idx")
    @JsonBackReference
    private ShopDetailEntity shopDetailJoin;

    // 메뉴 옵션 테이블과 1:N 매핑
    @OneToMany(mappedBy = "menuJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuOptionEntity> menuOptionJoin;

    // 주문 메뉴 옵션 테이블과 1:N 매핑
    @OneToMany(mappedBy = "menuJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersMenuEntity> ordersMenuJoin;

    // 장바구니 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_idx")
    @JsonBackReference
    private CartEntity cartJoin;

    // 이미지 파일 테이블과 1:N 매핑
    @OneToMany(mappedBy = "menuJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageFileEntity> imageFileJoin = new ArrayList<>(); // null값이면 코드작동 안하기 때문에 초기화 진행

    // 메뉴 생성과 동시에 이미지 추가
    public void addImage(ImageFileEntity image) {
        this.imageFileJoin.add(image);
        image.setMenuJoin(this);
    }

    // 기존 이미지 업데이트
    public void updateImages(List<ImageFileEntity> newImages) {
        this.imageFileJoin.clear();
        for (ImageFileEntity image : newImages) {
            this.addImage(image);
        }
    }

}