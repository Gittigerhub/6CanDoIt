package com.sixcandoit.roomservice.dto.Menu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class MenuOptionDTO {
    private Integer idx;                 // 기본 키

    private String menu_option_name;     // 메뉴 옵션 이름

    private int menu_option_price;       // 메뉴 옵션 가격

    private String active_yn;            // 활성유무(N:비활성,Y:활성)

}
