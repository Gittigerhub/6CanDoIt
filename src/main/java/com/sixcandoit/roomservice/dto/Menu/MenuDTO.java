package com.sixcandoit.roomservice.dto.Menu;

import com.sixcandoit.roomservice.constant.MenuCategory;
import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MenuDTO {

    private Integer idx;                 // 기본 키

    private MenuCategory menuCategory;        // 메뉴 카테고리(KF:한식, CF:중식, WF:양식, JF:일식, Dr:음료)

    private String menuName;             // 메뉴 이름

    private String menuContent;          // 메뉴 설명

    private int menuPrice;               // 메뉴 가격

    private String menuImg;              // 메뉴 이미지 경로

    private String menuOptionYn;         // 옵션 존재여부(N:없음, Y:있음)

    private String activeYn;             // 활성화유무(N:비활성, Y: 활성)

    private String menuSalesYn;          // 세일 여부(N:안함, Y:세일중)

    private String menuSaleType;         // 할인구분(NONE:없음,AMOUNT:할인액,PERSENT:할인율)

    private int originPrice;            // 원가

    private int menuSaleAmount;          // 할인액

    private int menuSalePercent;         // 할인율



    private List<MenuOptionDTO> menuOptionDTOList;

    private List<ImageFileDTO> menuImgDTOList;

    private LocalDateTime insDate;

    //통합 이미지 파일이 나온다면 그거로 설정
    public MenuDTO setMenuImgDTOList(List<ImageFileEntity> imageFileEntities) {
        ModelMapper modelMapper = new ModelMapper();

        List<ImageFileDTO> fileDTOS =
                imageFileEntities.stream().map(
                        menuImg -> modelMapper.map(menuImg, ImageFileDTO.class)
                ).collect(Collectors.toList());

                this.menuImgDTOList = fileDTOS;

                return this;
    }

}
