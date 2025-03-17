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
    private List<Integer> cartMenuIdxList;
    private String ordersPhone;
    private String ordersMemo;
}
// 주문목록에서 값 받아서 오는 DTO