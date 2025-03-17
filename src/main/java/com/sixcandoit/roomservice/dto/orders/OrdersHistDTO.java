package com.sixcandoit.roomservice.dto.orders;

import com.sixcandoit.roomservice.constant.OrdersStatus;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrdersHistDTO {
    private Integer idx;                     // 기본 키

    private int ordersPaymentType;           // 결제 타입(B:선 결제, A:체크아웃 시 결제)

    private OrdersStatus ordersStatus;        // 주문 상태(NEW 신규, CHECK 접수, COOKING 조리 중, CANCEL 취소, CLOSE 완료)

    private String ordersPhone;              // 연락받을 연락처

    private String ordersMemo;               // 주문 요청 사항

    private LocalDateTime insDate;           // 주문 일시

    private String roomName;           // 룸 이름

    private int totalAmount;           // 총 주문 금액

    private MemberDTO memberJoin;

    private String memberName;         // 작성자 이름

    private List<OrdersMenuDTO> ordersMenuJoin;

    private List<Integer> cartMenuIdxList;

    // setOrdersMenuDTOList 메서드 추가
    public void setOrdersMenuDTOList(List<OrdersMenuDTO> ordersMenuDTOList) {
        this.ordersMenuJoin = ordersMenuDTOList;
    }

}
// 주문목록에서 값 받아서 오는 DTO