package com.sixcandoit.roomservice.entity.orders;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
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
    private MemberEntity memberEntity;

    // 메뉴 테이블과 1:N 매핑
    @OneToMany(mappedBy = "cartJoin")
    private List<MenuEntity> menuJoin;

}