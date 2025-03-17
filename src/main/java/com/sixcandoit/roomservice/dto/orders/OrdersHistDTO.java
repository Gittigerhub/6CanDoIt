package com.sixcandoit.roomservice.dto.orders;

import com.sixcandoit.roomservice.constant.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrdersHistDTO {
    private Integer idx;                             // 주문 번호
    private String memberName;                       // 주문자명
    private String roomNumber;                       // 객실 번호
    private OrderStatus orderStatus;                 // 주문 상태
    private LocalDateTime insDate;                   // 주문 일시
    private String ordersPhone;                      // 연락받을 연락처
    private String ordersMemo;                       // 주문 요청 사항
    private List<OrdersMenuDTO> ordersMenuDTOList = new ArrayList<>();  // 주문 메뉴 목록
    private List<Integer> cartMenuIdxList;           // 카트메뉴 idx
}
// 주문목록에서 값 받아서 오는 DTO