package com.sixcandoit.roomservice.dto;

import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisementDTO {

    private Integer idx;                          // 기본 키

    private String adTitle;                       // 광고 제목

    private String adLinkUrl;                     // 광고 링크

    private LocalDateTime adStartDate;            // 광고 시작일

    private LocalDateTime adEndDate;              // 광고 종료일

    private String adState;                       // 상태(N:안함, Y:진행중)

    private int adHits;                           // 조회 수

    private OrganizationDTO organizationJoin;     // 조직 DTO

}