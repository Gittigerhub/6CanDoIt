package com.sixcandoit.roomservice.controller.orders;

import com.sixcandoit.roomservice.constant.OrdersStatus;
import com.sixcandoit.roomservice.dto.orders.OrdersHistDTO;
import com.sixcandoit.roomservice.service.orders.OrdersService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin")
public class AdminOrdersController {

    private final OrdersService ordersService;

    /* -----------------------------------------------------------------------------
       경로 : /orders/main
       인수 : Integer organ_idx, Model model
       출력 : room/main 페이지로 이동
       설명 : 룸 관리 메인 페이지를 반환
    ----------------------------------------------------------------------------- */
    // 룸 관리 메인 페이지
    @GetMapping("/orders/main")
    public String main(@RequestParam(required = false) Integer organ_idx, Model model) {
        if (organ_idx != null) {
            model.addAttribute("organ_idx", organ_idx);
        }
        return "orders/bo/main";
    }

    /* -----------------------------------------------------------------------------
   경로 : /orders/main
   인수 : Integer organ_idx, Model model
   출력 : room/main 페이지로 이동
   설명 : 룸 관리 메인 페이지를 반환
----------------------------------------------------------------------------- */
    // 룸 관리 메인 페이지
    @GetMapping("/orders/ho/main")
    public String homain(@RequestParam(required = false) Integer organ_idx, Model model) {
        if (organ_idx != null) {
            model.addAttribute("organ_idx", organ_idx);
        }
        return "orders/ho/main";
    }


    /* -----------------------------------------------------------------------------
       경로 : /orders/adordersList
       인수 : Pageable page, String type, String keyword, Integer organ_idx, Model model
       출력 : 주문 목록 페이지
       설명 : 관리자 주문 목록을 조회하여 페이지에 반환, 검색 기능을 포함
   ----------------------------------------------------------------------------- */
    //1. 주문 목록 페이지 (+검색 기능)
    @GetMapping("/orders/adordersList")
    public String ordersList(@PageableDefault(page = 1) Pageable page,
                           @RequestParam(value = "type", defaultValue = "") String type,
                           @RequestParam(value = "keyword", defaultValue = "") String keyword,
                           @RequestParam(required = false) Integer organ_idx,
                           Model model) {
        log.info("관리자 주문 목록 페이지");

        //주문 목록 조회
        Page<OrdersHistDTO> ordersHistDTOPage = ordersService.getAdminOrderList(type, keyword, organ_idx, page);

        //html에 필요한 페이지 정보를 받기
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(ordersHistDTOPage);

        //model에 데이터 추가
        model.addAttribute("orders", ordersHistDTOPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("organ_idx", organ_idx);
        model.addAllAttributes(pageInfo);

        return "orders/adordersList";
    }

    /* -----------------------------------------------------------------------------
       경로 : /orders/adordersList
       인수 : Pageable page, String type, String keyword, Integer organ_idx, Model model
       출력 : 주문 목록 페이지
       설명 : 관리자 주문 목록을 조회하여 페이지에 반환, 검색 기능을 포함
   ----------------------------------------------------------------------------- */
    //1. 주문 목록 페이지 (+검색 기능)
    @GetMapping("/orders/ho/adordersList")
    public String hoordersList(@PageableDefault(page = 1) Pageable page,
                             @RequestParam(value = "type", defaultValue = "") String type,
                             @RequestParam(value = "keyword", defaultValue = "") String keyword,
                             @RequestParam(required = false) Integer organ_idx,
                             Model model) {
        log.info("관리자 주문 목록 페이지");

        //주문 목록 조회
        Page<OrdersHistDTO> ordersHistDTOPage = ordersService.getAdminOrderList(type, keyword, organ_idx, page);

        //html에 필요한 페이지 정보를 받기
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(ordersHistDTOPage);

        //model에 데이터 추가
        model.addAttribute("orders", ordersHistDTOPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("organ_idx", organ_idx);
        model.addAllAttributes(pageInfo);

        return "orders/ho/adordersList";
    }


    /* -----------------------------------------------------------------------------
      경로 : /orders/{orderIdx}/status
      인수 : Integer orderIdx, String status
      출력 : 주문 상태 변경 결과 메시지
      설명 : 주문 상태를 변경하고 결과를 반환
  ----------------------------------------------------------------------------- */
    //2. 주문 상태 변경
    @PostMapping("/orders/{orderIdx}/status")
    @ResponseBody
    public ResponseEntity<String> updateOrdersStatus(@PathVariable Integer orderIdx,
                                                     String status) {

        log.info("주문 상태 변경", orderIdx, status);

        try {
            OrdersStatus newStatus = OrdersStatus.valueOf(status);
            ordersService.updateOrdersStatus(orderIdx, newStatus);
            return ResponseEntity.ok("주문 상태가 변경 되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("주문 상태 변경에 실패했습니다.");
        }
    }

    /* -----------------------------------------------------------------------------
      경로 : /orders/{orderIdx}/status
      인수 : Integer orderIdx, String status
      출력 : 주문 상태 변경 결과 메시지
      설명 : 주문 상태를 변경하고 결과를 반환
  ----------------------------------------------------------------------------- */
    //2. 주문 상태 변경
    @PostMapping("/orders/ho/{orderIdx}/status")
    @ResponseBody
    public ResponseEntity<String> houpdateOrdersStatus(@PathVariable Integer orderIdx,
                                                     String status) {

        log.info("주문 상태 변경", orderIdx, status);

        try {
            OrdersStatus newStatus = OrdersStatus.valueOf(status);
            ordersService.updateOrdersStatus(orderIdx, newStatus);
            return ResponseEntity.ok("주문 상태가 변경 되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("주문 상태 변경에 실패했습니다.");
        }
    }


    /* -----------------------------------------------------------------------------
       경로 : /orders/adordersRead/{orderIdx}
       인수 : Integer orderIdx, Model model
       출력 : 주문 상세 페이지
       설명 : 주문의 상세 정보를 조회하여 페이지에 반환
   ----------------------------------------------------------------------------- */
    //3. 주문 상세 정보 조회
    @GetMapping("/orders/adordersRead/{orderIdx}")
    public String readOrder(@PathVariable Integer orderIdx, Model model, @RequestParam(required = false) Integer organ_idx) {
        log.info("주문 상세 정보 조회:", orderIdx);

        try {
            OrdersHistDTO ordersHistDTO = ordersService.getAdminOrderDetail(orderIdx);
            model.addAttribute("orders", ordersHistDTO);
            model.addAttribute("organ_idx", organ_idx);
            return "orders/adordersRead";
        } catch (Exception e) {
            return "redirect:/admin/orders/ho/adordersList";
        }
    }

    /* -----------------------------------------------------------------------------
       경로 : /orders/ho/adordersRead/{orderIdx}
       인수 : Integer orderIdx, Model model
       출력 : 주문 상세 페이지
       설명 : 주문의 상세 정보를 조회하여 페이지에 반환
   ----------------------------------------------------------------------------- */
    //3. 주문 상세 정보 조회
    @GetMapping("/orders/ho/adordersRead/{orderIdx}")
    public String horeadOrder(@PathVariable Integer orderIdx, Model model, @RequestParam(required = false) Integer organ_idx) {
        log.info("주문 상세 정보 조회:", orderIdx);

        try {
            OrdersHistDTO ordersHistDTO = ordersService.getAdminOrderDetail(orderIdx);
            model.addAttribute("orders", ordersHistDTO);
            model.addAttribute("organ_idx", organ_idx);
            return "orders/ho/adordersRead";
        } catch (Exception e) {
            return "redirect:/admin/orders/ho/adordersList";
        }
    }


    /* -----------------------------------------------------------------------------
       경로 : /orders/ajax/adordersList
       인수 : String status, Pageable page
       출력 : 상태별 주문 목록
       설명 : 주문 상태별로 주문 목록을 조회하여 반환 (AJAX)
   ----------------------------------------------------------------------------- */
    //4. 상태별 주문 목록 조회(AJAX)
    @GetMapping("/orders/ho/ajax/adordersList")
    @ResponseBody
    public ResponseEntity<Page<OrdersHistDTO>> getOrdersByStatus(
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false) Integer organ_idx,
            @RequestParam(required = false) String keyword,
            @PageableDefault(page = 1) Pageable page) {

        log.info("상태별 주문 목록 조회 : {}, organ_idx: {}, keyword: {}", status, organ_idx, keyword);

        try {
            Page<OrdersHistDTO> ordersHistDTOPage =
                    ordersService.getOrdersByStatus(status, organ_idx, keyword, page);
            return ResponseEntity.ok(ordersHistDTOPage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
