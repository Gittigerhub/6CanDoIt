package com.sixcandoit.roomservice.controller.orders;


import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.Menu.MenuDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersMenuDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersPassDTO;
import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.cart.CartService;
import com.sixcandoit.roomservice.service.orders.OrdersService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")

public class MemberOrdersCotroller {

    private final ImageFileService imageFileService;
    private final OrdersService ordersService;
    private final CartService cartService;
    private final PageNationUtil pageNationUtil;

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


    // 주문하기
    // 주문하기는 별도의 읽기페이지 제작 X
    /* -----------------------------------------------------------------------------
        경로 : /orders
        인수 : @RequestBody OrdersHistDTO ordersHistDTO, Principal principal
        출력 : 주문성공 or 주문실패
        설명 : 사용자가 장바구니에서 주문하기 버튼을 클릭했을 때 장바구니 내에 메뉴들이 지워지며 주문내역으로 넘어감
    ----------------------------------------------------------------------------- */
    @PostMapping("/orders")
    public ResponseEntity<?> orders(@RequestBody OrdersPassDTO ordersPassDTO, Principal principal) {

        // 들어오는지 확인
        System.out.println("ordersHistDTO : " + ordersPassDTO.toString());
        System.out.println("사용자 : " + principal.getName());

        //저장
        Integer result
                = ordersService.createOrders(ordersPassDTO.getCartMenuIdxList(), principal.getName(),
                ordersPassDTO   .getOrdersPhone(), ordersPassDTO.getOrdersMemo());
        System.out.println("저장 후");

        //장바구니 메뉴 삭제
        for (Integer cartMenuIdx : ordersPassDTO.getCartMenuIdxList()) {
            cartService.deleteCartMenu(cartMenuIdx);
        }
        System.out.println("장바구니 메뉴 삭제 후");

        return new ResponseEntity<String>("주문 완료", HttpStatus.OK);

    }


    /* -----------------------------------------------------------------------------
        경로 : /orders
        인수 : @PageableDefault(page=1) Pageable page, Principal principal, Model model
        출력 : 사용자 주문내역
        설명 : 사용자가 장바구니에 주문한 내역들을 목록으로 볼 수있는 페이지 출력
    ----------------------------------------------------------------------------- */
    @GetMapping("/orders")
    public String ordersHist(@PageableDefault(page=1) Pageable page,
                             Principal principal, Model model) {

        System.out.println("진입");

        //로그인 체크
        if (principal == null) {
            log.info("로그인 필요");

            return "redirect:/member/login";
        }else {
            log.info("로그인 상태");
        }

        // 사용자 이메일
        String email = principal.getName();

        // 서비스에 주문 내역 조회 요청
        Page<OrdersDTO> ordersDTOS = ordersService.orderList(email, page);

        // 조회결과를 이용한 페이지 처리
        Map<String,Integer> pageInfo = pageNationUtil.Pagination(ordersDTOS);

        // 사용자가 예약한 객실정보
        RoomDTO roomDTO = ordersService.findMemberRoom(email);

        // join값 설정
        String join = "menu";

        // DTO들 리스트 생성
        List<MenuDTO> menu = new ArrayList<>();

        // Page<OrdersDTO> ordersDTOS 에서 ordersDTO객체 가져오기
        for (OrdersDTO ordersDTO : ordersDTOS) {
            // OrdersMenuDTO 가져오기
            List<OrdersMenuDTO> OrdersMenuDTO = ordersDTO.getOrdersMenuJoin();
            for (OrdersMenuDTO ordersMenu : OrdersMenuDTO) {
                // menuDTO 가져오기
                MenuDTO menuDTO = ordersMenu.getMenuJoin();

                // menu 가져온 menuDTO 추가
                menu.add(menuDTO);
            }

        }

        // 이미지 데이터를 담을 Map 생성 (menu의 idx를 key로 저장)
        Map<Integer, List<ImageFileDTO>> imageFileMap = new HashMap<>();
        Map<Integer, Boolean> repImageMap = new HashMap<>();

        for (MenuDTO menuDTO : menu) {
            // 이미지 조회
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(menuDTO.getIdx(), join);

            // Map에 저장 (menu의 idx를 key로 함)
            imageFileMap.put(menuDTO.getIdx(), imageFileDTOS);

            // 대표 사진 여부 확인 후 저장
            boolean hasRepImage = imageFileDTOS.stream()
                    .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

            repImageMap.put(menuDTO.getIdx(), hasRepImage);
        }

        //모델에 데이터 추가
        model.addAllAttributes(pageInfo);
        model.addAttribute("orders", ordersDTOS);
        model.addAttribute("roomDTO", roomDTO);
        model.addAttribute("imageFileMap", imageFileMap); // 메뉴별 이미지 리스트
        model.addAttribute("repImageMap", repImageMap); // 메뉴별 대표 사진 여부

        return "orders/ordersHist";

    }


    /* -----------------------------------------------------------------------------
      경로 : /orders/{orderIdx}/cancel
      인수 : Integer orderIdx, Principal principal
      출력 : 주문 취소 처리 결과
      설명 : 사용자가 주문을 취소할 수 있도록 주문 상태를 변경하고 그 결과를 반환
  ----------------------------------------------------------------------------- */
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