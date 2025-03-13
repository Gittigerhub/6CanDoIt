package com.sixcandoit.roomservice.dto.orders;

import com.sixcandoit.roomservice.constant.OrderStatus;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDTO {

    private Integer idx;                     // 기본 키

    private int ordersPaymentType;           // 결제 타입(B:선 결제, A:체크아웃 시 결제)

    private OrderStatus ordersStatus;        // 주문 상태(NEW 신규, CHECK 접수, COOKING 조리 중, CANCEL 취소, CLOSE 완료)

    private String ordersPhone;              // 연락받을 연락처

    private String ordersMemo;               // 주문 요청 사항

    private LocalDateTime insDate;           // 주문 일시

    private MemberDTO memberJoin;

    private List<OrdersMenuDTO> ordersMenuJoin;

    // ordersMenuJoin의 주문금액 총액을 구하기 위한 메서드
    public int getTotalAmount() {
        return ordersMenuJoin != null ?
                ordersMenuJoin.stream()
                        .mapToInt(menu -> menu.getMenuJoin().getMenuPrice() * menu.getCount())
                        .sum()
                : 0;
    }

}