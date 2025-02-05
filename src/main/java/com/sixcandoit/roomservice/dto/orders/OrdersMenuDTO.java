package com.sixcandoit.roomservice.dto.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class OrdersMenuDTO {

    private Integer idx;    //저장된 주문 메뉴

    private String menuName;    //메뉴 이름만

    private int orderPrice; //주문 가격

    private int count;  //수량

    private String menuImg; //대표 이미지
}
