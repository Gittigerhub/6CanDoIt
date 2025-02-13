package com.sixcandoit.roomservice.dto.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class CartOrdersDTO {

    private Integer cartMenuIdx;

    private List<CartOrdersDTO> ordersDTOList;
    //장바구니에서 여러개의 상품을 주문하기 때문에 장바구니 본인을 List로 가질 수 있도록
}
