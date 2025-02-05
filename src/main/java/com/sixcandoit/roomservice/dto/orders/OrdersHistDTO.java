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

    private Integer ordersIdx;   //주문 아이디

    private LocalDateTime ordersDate;   //주문 날짜

    private OrderStatus orderStatus;    //주문 상태

    private List<OrdersMenuDTO> ordersMenuDTOList
            = new ArrayList<>();

}
