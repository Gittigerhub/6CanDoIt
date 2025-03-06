package com.sixcandoit.roomservice.dto.cart;

import com.sixcandoit.roomservice.dto.Menu.MenuDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class CartDTO {

    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Integer idx;                    // 기본 키

    @Min(value = 1, message = "최소 수량은 1개입니다.")
    private int cartMenuCount;              // 메뉴 수량

    private MemberDTO memberJoin;        // 회원 DTO

    private List<MenuDTO> menuJoin;      // 메뉴들 DTO

}
