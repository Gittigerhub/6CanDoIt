package com.sixcandoit.roomservice.dto.orders;

import com.sixcandoit.roomservice.dto.Menu.MenuDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class OrdersMenuDTO {

    private Integer idx;                // 기본값

    @Min(value = 1, message = "최소 주문 수량은 1개입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개입니다.")
    private int count;                  // 수량

    private OrdersDTO ordersJoin;

    private MenuDTO menuJoin;

}