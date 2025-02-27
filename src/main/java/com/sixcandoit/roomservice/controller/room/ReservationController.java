package com.sixcandoit.roomservice.controller.room;

import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import com.sixcandoit.roomservice.service.room.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/res")
public class ReservationController {

    private final ReservationService reservationService;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    //등록폼으로 이동
    @GetMapping("/create")
    public String createForm(Model model) {
        log.info("등록 페이지로 이동....");

        // 룸 데이터 조회
        List<RoomEntity> roomEntities  = roomRepository.findAll(); // RoomEntity 리스트
        List<RoomDTO> roomDTOList = roomEntities.stream()
                .map(room -> modelMapper.map(room, RoomDTO.class))  // ModelMapper를 이용해 변환
                .collect(Collectors.toList());

        // 조회된 룸 데이터와 멤버 데이터를 모델에 추가
        model.addAttribute("roomDTOList", roomDTOList);

        return "reserve/insert"; // pageInsert.html 뷰 템플릿을 찾아서 렌더링합니다.
    }

    // 예외 발생 시 에러 메시지 전달
    @ExceptionHandler(RuntimeException.class)
    public String handleException(RuntimeException e, RedirectAttributes redirectAttributes) {
        log.error("예외 처리 예외 발생: {}", e.getMessage());  // 예외 로그 추가
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/res/create"; // 예약 등록 페이지로 리다이렉트
    }

    //등폭폼에서 입력한 내용을 저장
    @PostMapping("/create")
    public String createPage(@ModelAttribute ReservationDTO reservationDTO,
                             RedirectAttributes redirectAttributes) {
        log.info("등록 처리 후 목록페이지로 이동하는 postmapping 시작....");

        try {
            reservationService.reserveInsert(reservationDTO);
            redirectAttributes.addFlashAttribute("successMessage", "저장하였습니다.");
            log.info("등록 처리 후....");
        } catch (RuntimeException e) {
            log.error("등록 예외 발생: {}", e.getMessage());  // 예외 메시지 확인 로그 추가
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        log.info("목록페이지로 이동...");
        return "redirect:/res/list";
    }

    //목록페이지로 이동
    @GetMapping("/list")
    public String getAllPages(@RequestParam(name = "sdate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sdate,
                              @RequestParam(name = "edate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate edate,
                              Model model) {
        log.info("데이터 조회 후 목록페이지로 이동....");

        // 리다이렉트 후 전달된 성공 또는 실패 메시지를 받아옵니다.
        if (model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", model.getAttribute("errorMessage"));
        }

        List<ReservationDTO> reserveDTOList = reservationService.reserveList(sdate, edate);

        model.addAttribute("List", reserveDTOList);
        model.addAttribute("sdate", sdate);
        model.addAttribute("edate", edate);

        return "reserve/list"; // page-list.html 뷰 템플릿을 찾아서 렌더링합니다.
    }

    //관리자용 예약 목록 리스트
    @GetMapping("/reserve")
    public String reslist(@RequestParam(name = "sdate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sdate,
                              @RequestParam(name = "edate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate edate,
                              Model model) {
        log.info("데이터 조회 후 목록페이지로 이동....");

        // 리다이렉트 후 전달된 성공 또는 실패 메시지를 받아옵니다.
        if (model.containsAttribute("errorMessage")) {
            // 에러 메시지가 있으면 모달을 띄워주도록 합니다.
            model.addAttribute("errorMessage", model.getAttribute("errorMessage"));
        }

        List<ReservationDTO> reserveDTOList = reservationService.reserveList(sdate, edate);

        model.addAttribute("List", reserveDTOList);
        model.addAttribute("sdate", sdate);
        model.addAttribute("edate", edate);

        return "room/reserve";
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

        // 룸 데이터 조회
        List<RoomEntity> roomEntities  = roomRepository.findAll(); // RoomEntity 리스트
        List<RoomDTO> roomDTOList = roomEntities.stream()
                .map(room -> modelMapper.map(room, RoomDTO.class))  // ModelMapper를 이용해 변환
                .collect(Collectors.toList());

        model.addAttribute("data", reserveDTO);
        model.addAttribute("roomDTOList", roomDTOList);
        return "reserve/update"; // update-form.html 뷰 템플릿을 찾아서 렌더링합니다.
    }

    // 예약 수정 처리
    @PostMapping("/update")
    public String updatePage(@ModelAttribute ReservationDTO updatedReserveDTO,
                             RedirectAttributes redirectAttributes) {
        log.info("수정 처리 후 목록페이지로 이동....");

        try {
            reservationService.reserveUpdate(updatedReserveDTO);
            redirectAttributes.addFlashAttribute("successMessage", "수정하였습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/res/list";
    }

    // 삭제 처리
    @GetMapping("/delete/{idx}")
    public String deletePage(@PathVariable Integer idx) {
        log.info("삭제 처리 후 목록페이지로 이동....");
        reservationService.reserveDelete(idx);

        //redirectAttributes.addFlashAttribute("successMessage",
        //        "삭제하였습니다.");
        return "redirect:/res/list";
    }

}
