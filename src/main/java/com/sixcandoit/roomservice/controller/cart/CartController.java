package com.sixcandoit.roomservice.controller.cart;

import com.sixcandoit.roomservice.dto.cart.CartDetailDTO;
import com.sixcandoit.roomservice.dto.cart.CartMenuDTO;
import com.sixcandoit.roomservice.dto.cart.CartOrdersDTO;
import com.sixcandoit.roomservice.service.cart.CartMenuService;
import com.sixcandoit.roomservice.service.cart.CartService;
import com.sixcandoit.roomservice.service.menu.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartService cartService;
    private final CartMenuService cartMenuService;
    private final MenuService menuService;


    //장바구니 등록
    @PostMapping("/cart")
    public ResponseEntity registerCart(Integer idx, BindingResult bindingResult,
                                    Principal principal) {

        //System.out.println(cartMenuDTO);
        System.out.println(principal);

        //유효성 검사
        if (bindingResult.hasErrors()) {
            StringBuffer sb = new StringBuffer();

            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        //값이 잘 넘어왔다면
        // 이메일로 회원을 찾고 장바구니에 메뉴 추가
        String email = principal.getName();

        try {
            //장바구니에 추가
            Integer cartMenuIdx = cartService.addCart(idx, email);
            //저장 후 브라우저로 재전송
            return new ResponseEntity<Integer>(cartMenuIdx, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/cart/cartlist")
    public String getCart(Principal principal, Model model) {
        // 사용자 이메일을 이용해 장바구니 정보 가져오기
        String email = principal.getName();

        //사용자에게 보여줄 장바구니 목록
        List<CartDetailDTO> cartDetailDTOList =
                cartService.cartDetailDTOList(email);

        //사용자에게 보여줄 장바구니 목록 중에 CartDetailDTO(꼭 필요한 정보만 가공한 DTO)로 담은 List
        model.addAttribute("cartDetailDTOList", cartDetailDTOList);

        return "cart/cartlist";
    }

    //변경
    @PostMapping("/cartmenu")
    public ResponseEntity updateCartMenu(@Valid CartMenuDTO cartMenuDTO, BindingResult bindingResult,
                                         Principal principal) {
        String memberEmail = principal.getName();

        log.info("수량 변경을 위해 넘어온 값 : " + cartMenuDTO);
        if (bindingResult.hasErrors()) {
            StringBuffer sb = new StringBuffer();

            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<Integer>(Integer.valueOf(sb.toString()), HttpStatus.BAD_REQUEST);
        }

        //service에서 CartMenuidx를 통해서 CartMenu를 찾아
        //CartMenu의 수량을 현재 있는 cartMenuIdxDTO의 Count로 변경

        try {
            cartMenuService.updatecartMenuIdx(cartMenuDTO, memberEmail);
        } catch (Exception e) {
            return new ResponseEntity<String>("올바르지 않은 형식입니다. 고객 센터에 문의하세요", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(cartMenuDTO.getMenuidx(), HttpStatus.OK);
    }

    @DeleteMapping("/cart/cart/{cartmenuidx}")
    public ResponseEntity deleteCartMenu(@PathVariable("cartmenuidx") Integer cartmenuidx,
                                         Principal principal) {
        if (!cartService.validateCartMenu(cartmenuidx, principal.getName())) {
            return new ResponseEntity<String>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        //장바구니 메뉴 삭제
        cartService.deleteCartMenu(cartmenuidx);
        return new ResponseEntity<Integer>(cartmenuidx, HttpStatus.OK);
    }

    @PostMapping("/cart/cartlist")
    public ResponseEntity orderCartMenu(@RequestBody CartOrdersDTO cartOrdersDTO, Principal principal) {
        log.info(cartOrdersDTO);
        List<CartOrdersDTO> cartOrdersDTOList = cartOrdersDTO.getOrdersDTOList();

        if (cartOrdersDTOList == null || cartOrdersDTOList.size() == 0) {
            return new ResponseEntity<String>("주문할 메뉴를 선택해주세요.", HttpStatus.FORBIDDEN);
        }
        for (CartOrdersDTO cartOrders : cartOrdersDTOList) {
            if (!cartService.validateCartMenu(cartOrders.getCartMenuIdx(), principal.getName())) {
                return new ResponseEntity("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        Integer orderidx = cartService.orderCartMenu(cartOrdersDTOList, principal.getName());

        return new ResponseEntity<Integer>(orderidx, HttpStatus.OK);
    }
}
