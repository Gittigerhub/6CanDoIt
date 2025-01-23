package com.sixcandoit.roomservice.dto.office;

import com.sixcandoit.roomservice.dto.member.AdminDTO;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {

    private Integer idx;                    // 증감 키

    private String organName;               // 조직 명

    private String organType;               // 조직 타입(HO:본사, BO:지사, SHOP:매장)

    private String organAddress;            // 조직 주소

    private String activeYn;                // 활성 유무(N: 비 활성화, Y: 활성화)

    private ShopDetailDTO shopDetailDTO;    // 매장 상세 DTO

    private AdminDTO adminDTO;              // 관리자 회원 DTO






}