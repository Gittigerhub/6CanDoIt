package com.sixcandoit.roomservice.dto.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrdersPassDTO {

    // 받아오는 메뉴 idx 리스트
    private List<Integer> cartMenuIdxList;

    // 주문완료 연락받을 연락처
    private String ordersPhone;

    // 주문자 요청메모
    private String ordersMemo;

}
// 주문목록에서 값 받아서 오는 DTO