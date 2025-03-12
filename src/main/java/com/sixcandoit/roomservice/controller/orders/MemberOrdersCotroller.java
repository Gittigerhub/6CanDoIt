package com.sixcandoit.roomservice.controller.orders;


import com.sixcandoit.roomservice.service.cart.CartService;
import com.sixcandoit.roomservice.service.orders.OrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")

public class MemberOrdersCotroller {

    private final OrdersService ordersService;
    private final CartService cartService;

    //주문하기
    //주문하기는 별도의 읽기페이지 제작 X
//    @PostMapping("/orders")
//    public ResponseEntity orders(@Valid OrdersDTO ordersDTO, BindingResult bindingResult, Principal principal) {
//
//        //유효성 검사
//        //메뉴 idx, count 존재 여부 확인
//        if (bindingResult.hasErrors()) {
//            StringBuffer errors = new StringBuffer();
//
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();  //각 필드별 에러
//
//            for (FieldError fieldError : fieldErrors) {
//                log.info("필드 : " + fieldError.getField() + "메시지 : " + fieldError.getDefaultMessage());
//                errors.append(fieldError.getDefaultMessage());
//            }
//            log.info(errors.toString());
//            return new ResponseEntity<Integer>(Integer.valueOf(errors.toString()), HttpStatus.BAD_REQUEST);
//        }
//
//        //로그인한 사람만 해당 주소를 이용할 수 있도록
//        log.info("사용자가 현재 주문하려는 내용 : " + ordersDTO);
//
//        //저장
//        Integer result
//                = ordersService.createOrders(ordersDTO, principal.getName());
//
//
//        return new ResponseEntity<String>("주문 완료", HttpStatus.OK);
//    }

    //주문하기
    //주문하기는 별도의 읽기페이지 제작 X
    @PostMapping("/orders")
    public ResponseEntity<?> orders(@RequestBody List<Integer> cartMenuIdxList, Principal principal,
                                    String ordersPhone, String ordersMemo) {

        // 들어오는지 확인
        System.out.println("cartMenuIdxList : " + cartMenuIdxList.toString());
        System.out.println("사용자 : " + principal.getName());

        //저장
        Integer result
                = ordersService.createOrders(cartMenuIdxList, principal.getName(), ordersPhone, ordersMemo);
        System.out.println("저장 후");

        //장바구니 메뉴 삭제
        for (Integer cartMenuIdx : cartMenuIdxList) {
            cartService.deleteCartMenu(cartMenuIdx);
        }
        System.out.println("장바구니 메뉴 삭제 후");

        return new ResponseEntity<String>("주문 완료", HttpStatus.OK);

    }

    @GetMapping({"/orders", "/orders/{page}"})
    public String ordersHist(@PathVariable("page") Optional<Integer> page,
                             Principal principal, Model model) {

        log.info("진입");

        //로그인 체크
        if (principal == null) {
            log.info("로그인 필요");

            return "redirect:/member/login";
        }else {
            log.info("로그인 상태");
        }

        // 페이지 정보 설정 (기본값: 0)
        int pageNumber = page.orElse(0);
        Pageable pageable = PageRequest.of(pageNumber, 10);

//        //주문 내역 조회
//        String email = principal.getName();
//        Page<OrdersHistDTO> ordersHistDTOPage
//                = ordersService.getOrderList(email, pageable);

        //모델에 데이터 추가
//        model.addAttribute("orders", ordersHistDTOPage);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage",5);

        return "orders/ordersHist";
    }

    //주문 취소
    @PostMapping("/orders/{orderIdx}/cancel")
    public ResponseEntity ordersCancel(@PathVariable("orderIdx") Integer orderIdx, Principal principal) {

        //orderIdx = orderIdx
        //orderIdx를 삭제 후 ordersMenu에서 orderIdx를 참조하는 ordersMenu를 삭제

        log.info("취소할 주문 번호 :" + orderIdx);
        log.info("취소할 주문번호에 있는 메뉴 :");

        if (!ordersService.validateOrders(orderIdx, principal.getName())) {
            //담긴 메뉴가 아니다
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        //취소를 한다.
        //orderStatus를 cancel로 바꾸기

        ordersService.cancelOrders(orderIdx);

        return new ResponseEntity<Integer>(orderIdx, HttpStatus.OK);
    }
}
