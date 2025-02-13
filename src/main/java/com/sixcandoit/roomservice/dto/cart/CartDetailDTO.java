package com.sixcandoit.roomservice.dto.cart;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class CartDetailDTO {

    private Integer cartMenuIdx;     //장바구니에 담긴 메뉴 아이디

    private String menuName;    //메뉴명

    private int menuPrice;  //메뉴 가격

    private int count;  //주문 수량

    private String menuImg;    //메뉴 이미지 경로
}
