package com.sixcandoit.roomservice.dto.cart;

import com.sixcandoit.roomservice.dto.Menu.MenuDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class CartMenuDTO {

    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Integer menuidx;

    @Min(value = 1, message = "최소 수량은 1개입니다.")
    private int count;

    private MenuDTO menuDTO;    // menuDTO 추가

}