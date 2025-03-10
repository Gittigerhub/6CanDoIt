package com.sixcandoit.roomservice.controller.orders;

import com.sixcandoit.roomservice.constant.OrderStatus;
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

    //1. 주문 목록 페이지 (+검색 기능)
    @GetMapping("/orders/adordersList")
    public String ordersList(@PageableDefault(page = 1) Pageable page,
                           @RequestParam(value = "type", defaultValue = "") String type,
                           @RequestParam(value = "keyword", defaultValue = "") String keyword,
                           Model model) {
        log.info("관리자 주문 목록 페이지");

        //주문 목록 조회
        Page<OrdersHistDTO> ordersHistDTOPage = ordersService.getAdminOrderList(type, keyword, page);

        //html에 필요한 페이지 정보를 받기
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(ordersHistDTOPage);

        //model에 데이터 추가
        model.addAttribute("orders", ordersHistDTOPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAllAttributes(pageInfo);

        return "orders/adordersList";
    }

    //2. 주문 상태 변경
    @PostMapping("/orders/{orderIdx}/status")
    @ResponseBody
    public ResponseEntity<String> updateOrdersStatus(@PathVariable Integer orderIdx,
                                                     String status) {

        log.info("주문 상태 변경", orderIdx, status);

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status);
            ordersService.updateOrderStatus(orderIdx, newStatus);
            return ResponseEntity.ok("주문 상태가 변경 되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("주문 상태 변경에 실패했습니다.");
        }
    }

    //3. 주문 상세 정보 조회
    @GetMapping("/orders/adordersRead/{orderIdx}")
    public String readOrder(@PathVariable Integer orderIdx, Model model) {
        log.info("주문 상세 정보 조회:", orderIdx);

        try {
            OrdersHistDTO ordersHistDTO = ordersService.getAdminOrderDetail(orderIdx);
            model.addAttribute("orders", ordersHistDTO);
            return "orders/adordersRead";
        } catch (Exception e) {
            return "redirect:/admin/orders/adordersList";
        }
    }

    //4. 상태별 주문 목록 조회(AJAX)
    @GetMapping("/orders/ajax/adordersList")
    @ResponseBody
    public ResponseEntity<Page<OrdersHistDTO>> getOrdersByStatus(
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @PageableDefault(page = 1) Pageable page) {

        log.info("상태별 주문 목록 조회 : ", status);

        try {
            Page<OrdersHistDTO> ordersHistDTOPage =
                    ordersService.getOrdersByStatus(status, page);
            return ResponseEntity.ok(ordersHistDTOPage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
