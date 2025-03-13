package com.sixcandoit.roomservice.dto.office;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ShopDetailDTO {
    private Integer idx;                  // 기본 키
    private String tel;                   // 매장 연락처
    private LocalTime openTime;              // 매장 오픈시간
    private LocalTime closeTime;             // 매장 마감시간
    private String restDay;               // 매장 휴일
    private int openState;                // 매장 상태(0:영업중, 1종료)
    private int partnerState;             // 매장 제휴상태(0:제휴중, 1:제휴종료, 3:제휴준비중)
    private int type;                     // 매장 타입(0:직영, 1:가맹)
    private String bankNum;               // 매장 계좌번호
    private String bankName;              // 매장 계좌 은행명
    private String bankOwner;             // 매장 계좌 소유주
    private String activeYn;              // 매장 활성화(N:비활성화, Y:활성화)
    private float dayFee;                // 매장 일별 수수료
    private float dayFeePercent;         // 매장 일별 수수료 %
    private OrganizationDTO organizationJoin;   // 조직 정보
}