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
public class OrdersHistDTO {

    private String ordersPhone;                      // 연락받을 연락처

    private String ordersMemo;                       // 주문 요청 사항

    private List<Integer> cartMenuIdxList;           // 카트메뉴  idx

}
// 주문목록에서 값 받아서 오는 DTO