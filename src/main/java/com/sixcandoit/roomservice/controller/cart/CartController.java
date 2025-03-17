package com.sixcandoit.roomservice.controller.cart;

import com.sixcandoit.roomservice.dto.cart.CartDetailDTO;
import com.sixcandoit.roomservice.dto.cart.CartMenuDTO;
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
@RequestMapping("/member")
public class CartController {

    private final CartService cartService;
    private final CartMenuService cartMenuService;
    private final MenuService menuService;


    /* -----------------------------------------------------------------------------
       경로 : /member/cart
       인수 : Integer idx, int count, Principal principal
       출력 : 장바구니에 메뉴 추가
       설명 : 장바구니에 메뉴를 추가하고, 수량을 지정한 후 장바구니 추가 결과를 리턴
   ----------------------------------------------------------------------------- */
    //장바구니 등록
    @PostMapping("/cart")
    public ResponseEntity<?> registerCart(Integer idx, @RequestParam(value = "count", defaultValue = "0") int count, Principal principal) {

        System.out.println("메뉴 Idx : " + idx);
        System.out.println("메뉴 수량 : " + count);
        System.out.println("로그인 정보 : " + principal.getName());

//        //유효성 검사 >>  BindingResult 사용하려면 @Valid ~DTO 객체를 받아야함
//        if (bindingResult.hasErrors()) {
//            StringBuffer sb = new StringBuffer();
//
//            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
//
//            for (FieldError fieldError : fieldErrorList) {
//                sb.append(fieldError.getDefaultMessage());
//            }
//            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
//        }

        // 값이 잘 넘어왔다면
        // 이메일로 회원을 찾고 장바구니에 메뉴 추가
        String email = principal.getName();
        System.out.println("email 정보 : " + email);

        try {
            //장바구니에 추가
            Integer cartMenuIdx = cartService.addCart(idx, email, count);
            //저장 후 브라우저로 재전송
            return new ResponseEntity<Integer>(cartMenuIdx, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /* -----------------------------------------------------------------------------
       경로 : /member/cart/cartlist
       인수 : Principal principal, Model model
       출력 : 장바구니 목록 페이지
       설명 : 로그인한 사용자에 대한 장바구니 목록을 가져와서 화면에 전달
   ----------------------------------------------------------------------------- */
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


    /* -----------------------------------------------------------------------------
       경로 : /member/cart/cartmenu
       인수 : CartMenuDTO cartMenuDTO, BindingResult bindingResult, Principal principal
       출력 : 수량 변경 결과
       설명 : 장바구니 메뉴의 수량을 변경
   ----------------------------------------------------------------------------- */
    //변경
    @PostMapping("/cart/cartmenu")
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


    /* -----------------------------------------------------------------------------
       경로 : /member/cart/cartlist/delete/{cartmenuidx}
       인수 : Integer cartmenuidx, Principal principal
       출력 : 장바구니 목록 페이지로 리다이렉트
       설명 : 장바구니 메뉴를 삭제 후 장바구니 목록 페이지로 리다이렉트
   ----------------------------------------------------------------------------- */
    @PostMapping("/cart/cartlist/delete/{cartmenuidx}")
    public String deleteCartMenu(@PathVariable("cartmenuidx") Integer cartmenuidx,
                                         Principal principal) {
        if (!cartService.validateCartMenu(cartmenuidx, principal.getName())) {
            return "redirect:/member/cart/cartlist?error";
        }
        //장바구니 메뉴 삭제
        cartService.deleteCartMenu(cartmenuidx);
        return "redirect:/member/cart/cartlist";    //삭제 후 장바구니 목록 페이지로 리다이렉트
    }

    // 안씀
//    @PostMapping("/cart/cartlist")
//    public ResponseEntity orderCartMenu(@RequestBody CartOrdersDTO cartOrdersDTO, Principal principal) {
//
//        log.info(cartOrdersDTO);
//        List<CartOrdersDTO> cartOrdersDTOList = cartOrdersDTO.getCartOrdersDTOList();
//
//        if (cartOrdersDTOList == null || cartOrdersDTOList.size() == 0) {
//            return new ResponseEntity<String>("주문할 메뉴를 선택해주세요.", HttpStatus.FORBIDDEN);
//        }
//
//        for (CartOrdersDTO cartOrders : cartOrdersDTOList) {
//            if (!cartService.validateCartMenu(cartOrders.getCartMenuIdx(), principal.getName())) {
//                return new ResponseEntity("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
//            }
//        }
//        Integer orderidx = cartService.orderCartMenu(cartOrdersDTOList, principal.getName());
//
//        return new ResponseEntity<Integer>(orderidx, HttpStatus.OK);
//    }


    /* -----------------------------------------------------------------------------
       경로 : /member/cart/count
       인수 : Principal principal
       출력 : 장바구니 메뉴 개수
       설명 : 사용자의 장바구니에 담긴 메뉴 개수를 반환
   ----------------------------------------------------------------------------- */
    //장바구니 개수 반환하는 메서드 추가
    @GetMapping("/cart/count")
    @ResponseBody
    public ResponseEntity<Integer> getCartCount(Principal principal) {
        String email = principal.getName();
        List<CartDetailDTO> cartList = cartService.cartDetailDTOList(email);
        return ResponseEntity.ok(cartList.size());
    }

}
//현재 contentType: 'application/json'으로 요청을 보내고 있어서
//Spring이 @RequestParam이 아니라 @RequestBody로 받아야 매핑할 수 있음.