package com.sixcandoit.roomservice.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MenuDTO {
    private Integer idx;                 // 기본 키

    private String menuName;             // 메뉴 이름

    private String menuContent;          // 메뉴 설명

    private int menuPrice;               // 메뉴 가격

    private String menuImg;              // 메뉴 이미지 경로

    private String menuOptionYn;         // 옵션 존재여부(N:없음, Y:있음)

    private String activeYn;             // 활성화유무(N:비활성, Y: 활성)

    private String menuSalesYn;          // 세일 여부(N:안함, Y:세일중)

    private String menuSaleType;         // 할인구분(NONE:없음,AMOUNT:할인액,PERSENT:항인율)

    private int menuSaleAmount;          // 할인액

    private int menuSalePercent;         // 할인율

    private List<MenuOptionDTO> menuOptionDTOList;

//    private List<MenuImgDTO> menuImgDTOList;

    private LocalDateTime insDate;

    //통합 이미지 파일이 나온다면 그거로 설정
//    public MenuDTO setMenuImgDTOList(List<MenuImg> menuImgList) {
//        ModelMapper modelMapper = new ModelMapper();
//
//        List<MenuImgDTO> menuImgDTOS =
//                menuImgList.stream().map(
//                        itemImg -> modelMapper.map(itemImg, MenuImgDTO.class)
//                ).collect(Collectors.toList();
//
//                this.menuImgDTOList = menuImgDTOS;
//
//                return this;
//    }

}
