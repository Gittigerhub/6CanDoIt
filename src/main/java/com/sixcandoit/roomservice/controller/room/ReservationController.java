package com.sixcandoit.roomservice.controller.room;

import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.service.room.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log
@RequestMapping("/res")
public class ReservationController {

    private final ReservationService reservationService;

    //등록폼으로 이동
    @GetMapping("/create")
    public String createForm(Model model) {
        log.info("등록 페이지로 이동....");

        return "reserve/insert"; // pageInsert.html 뷰 템플릿을 찾아서 렌더링합니다.
    }

    //등폭폼에서 입력한 내용을 저장
    @PostMapping("/create")
    public String createPage(@ModelAttribute ReservationDTO reservationDTO,
                             RedirectAttributes redirectAttributes) {
        log.info("등록 처리 후 목록페이지로 이동....");
        reservationService.reserveInsert(reservationDTO);

        redirectAttributes.addFlashAttribute("successMessage",
                "저장하였습니다.");

        return "redirect:/res/list";
    }

    //목록페이지로 이동
    @GetMapping("/list")
    public String getAllPages(@RequestParam(name = "sdate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sdate,
                              @RequestParam(name = "edate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate edate,
                              Model model) {
        log.info("데이터 조회 후 목록페이지로 이동....");
        List<ReservationDTO> reserveDTOList = reservationService.reserveList(sdate, edate);

        model.addAttribute("List", reserveDTOList);
        model.addAttribute("sdate", sdate);
        model.addAttribute("edate", edate);

        return "reserve/list"; // page-list.html 뷰 템플릿을 찾아서 렌더링합니다.
    }

    @GetMapping("/detail")
    public String getPageById(@RequestParam("idx") Integer idx, Model model) {
        log.info("데이터 조회 후 상세페이지로 이동....");
        ReservationDTO reserveDTO = reservationService.reserveRead(idx);

        model.addAttribute("data", reserveDTO);
        return "reserve/detail"; // page-details.html 뷰 템플릿을 찾아서 렌더링합니다.
    }

    @GetMapping("/update/{idx}")
    public String updateForm(@PathVariable Integer idx, Model model) {
        log.info("데이터 조회 후 수정페이지로 이동....");
        ReservationDTO reserveDTO = reservationService.reserveRead(idx);

        model.addAttribute("data", reserveDTO);
        return "reserve/update"; // update-form.html 뷰 템플릿을 찾아서 렌더링합니다.
    }

    //수정 폼에서 제출된 데이터를 받아와서 PageService를 통해 데이터를 수정하고, 수정이 성공하면
    //"/getAll"로 리다이렉트합니다. 이때, 성공 메시지인 "수정하였습니다."를 Flash Attribute로
    //추가하여 다음 페이지에 전달합니다.
    @PostMapping("/update")
    public String updatePage(@ModelAttribute ReservationDTO updatedReserveDTO,
                             RedirectAttributes redirectAttributes) {
        log.info("수정 처리 후 목록페이지로 이동....");
        reservationService.reserveUpdate(updatedReserveDTO);

        redirectAttributes.addFlashAttribute("successMessage",
                "수정하였습니다.");
        return "redirect:/res/list";
    }

    //특정 CRUD 데이터를 삭제하고, 삭제가 성공하면 "/getAll"로 리다이렉트합니다. 이때, 성공 메시지인
    //"삭제하였습니다."를 Flash Attribute로 추가하여 다음 페이지에 전달합니다.
    @GetMapping("/delete/{idx}")
    public String deletePage(@PathVariable Integer idx) {
        log.info("삭제 처리 후 목록페이지로 이동....");
        reservationService.reserveDelete(idx);

        //redirectAttributes.addFlashAttribute("successMessage",
        //        "삭제하였습니다.");
        return "redirect:/res/list";
    }
}
