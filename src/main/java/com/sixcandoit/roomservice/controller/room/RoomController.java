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

@Controller
@RequiredArgsConstructor
@Log
public class RoomController {

    // final 선언
    private final RoomService roomService;

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

        // 등록 처리
        roomService.roomRegister(roomDTO);

        return "redirect:/room/register";
    }
    // 룸 수정
    // 룸 삭제
    // 룸 목록
    // 룸 상세보기
}
