package com.sixcandoit.roomservice.controller.room;

import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.service.room.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log
public class RoomController {

    // final 선언
    private final RoomService roomService;

    // 룸 전체 목록
    @GetMapping("/room/list")
    private String list(Model model){
        log.info("모든 객실 정보를 읽어온다...");
        List<RoomDTO> roomDTOList = roomService.roomList();

        model.addAttribute("roomDTOList", roomDTOList);
        return "room/list";
    }

    // 룸 등록
    @GetMapping("/room/register")
    public String register(Model model){
        log.info("룸 등록 페이지로 이동합니다.");

        model.addAttribute("roomDTO", new RoomDTO()); // 빈 RoomDTO 객체 전달

        return "room/register";
    }

    // 룸 등록 저장 처리
    @PostMapping("/room/register")
    public String registerProc(@Valid @ModelAttribute RoomDTO roomDTO){
        log.info("새로운 룸을 등록합니다.");
        // roomWifi, roomTv, roomAir, roomBath가 null일 경우 N으로 설정
        if (roomDTO.getRoomWifi() == null) {
            roomDTO.setRoomWifi("N");
        }
        if (roomDTO.getRoomTv() == null) {
            roomDTO.setRoomTv("N");
        }
        if (roomDTO.getRoomAir() == null) {
            roomDTO.setRoomAir("N");
        }
        if (roomDTO.getRoomBath() == null) {
            roomDTO.setRoomBath("N");
        }
        if (roomDTO.getRoomBreakfast() == null) {
            roomDTO.setRoomBreakfast("N");
        }
        if (roomDTO.getRoomSmokingYn() == null) {
            roomDTO.setRoomSmokingYn("N");
        }
        // 등록 처리
        roomService.roomRegister(roomDTO);

        return "redirect:/room/list";
    }
    // 룸 수정
    // 룸 삭제
    // 룸 상세보기
    @GetMapping("/qna/detail")
    public String detail(@RequestParam Integer idx, Model model){
        log.info("개별 데이터를 읽는 중입니다.");
        RoomDTO roomDTO = roomService.roomDetail(idx);

        log.info("개별 데이터를 페이지에 전달하는 중입니다.");
        model.addAttribute("roomDTO", roomDTO);

        return "room/detail";
    }
}
