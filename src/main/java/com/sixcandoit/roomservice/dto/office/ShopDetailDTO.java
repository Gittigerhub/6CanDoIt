package com.sixcandoit.roomservice.dto.office;

import com.sixcandoit.roomservice.dto.Menu.MenuDTO;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShopDetailDTO {
    private Integer idx;                        // 기본 키
    private LocalTime openTime;                 // 매장 오픈시간
    private LocalTime closeTime;                // 매장 마감시간
    private String restDay;                     // 매장 휴일
    private int openState;                      // 매장 상태(0:영업중, 1종료)
    private int partnerState;                   // 매장 제휴상태(0:제휴중, 1:제휴종료, 3:제휴준비중)
    private int type;                           // 매장 타입(0:직영, 1:가맹)
    private String bankNum;                     // 매장 계좌번호
    private String bankName;                    // 매장 계좌 은행명
    private String bankOwner;                   // 매장 계좌 소유주
    private float dayFee;                       // 매장 일별 수수료
    private float dayFeePercent;                // 매장 일별 수수료 %
    private OrganizationDTO organizationJoin;   // 조직 정보
    private List<MenuDTO> menuJoin;             // 메뉴 정보
}