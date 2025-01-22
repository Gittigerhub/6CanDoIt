package com.sixcandoit.roomservice.dto.office;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShopDetailDTO {

    private Integer idx;                        // 증감 키

    private String shopDetailTel;               // 매장 연락처

    private String shopDetailOpenTime;          // 매장 오픈 시간

    private String shopDetailCloseTime;         // 매장 마감 시간

    private String shopDetailRestDay;           // 매장 휴일

    private int shopDetailOpenState;            // 매장 오픈 여부(0:영업 중, 1:종료)

    private int shopDetailPartnerState;         // 매장 제휴 상태(0:제휴 중, 1:제휴 종료, 2:제휴 준비중)

    private int shopDetailType;                 // 매장 타입(0:직영, 1:가맹)

    private String bankNum;                     // 계좌 번호

    private String bankName;                    // 계좌 은행명

    private String bankOwner;                   // 계좌 소유주

    private String activeYn;                    // 활성 유무(N:비 활성화, Y:활성화)

    private float dayFee;                       // 일일 수수료

    private float dayFeePercent;                // 일일 수수료 %

    private OrganizationDTO organizationDTO;    // 조직 DTO

}