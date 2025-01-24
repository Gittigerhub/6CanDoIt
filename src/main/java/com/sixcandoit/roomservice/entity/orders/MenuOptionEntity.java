package com.sixcandoit.roomservice.entity.orders;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
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
@Table(name = "menu_option")
public class MenuOptionEntity extends BaseEntity {

    @Id
    @Column(name = "menu_option_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                 // 기본 키

    @Column(name = "menu_option_name")
    private String menu_option_name;     // 메뉴 옵션 이름

    @Column(name = "menu_option_price")
    private int menu_option_price;       // 메뉴 옵션 가격

    @Column(name = "active_yn")
    private String active_yn;            // 활성유무(N:비활성,Y:활성)


    // 메뉴 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_idx")
    @JsonBackReference
    private MenuEntity menuJoin;

}