package com.sixcandoit.roomservice.controller.event;

import com.sixcandoit.roomservice.dto.event.EventDTO;
import com.sixcandoit.roomservice.dto.event.MemberPointDTO;
import com.sixcandoit.roomservice.entity.event.EventEntity;
import com.sixcandoit.roomservice.repository.event.EventRepository;
import com.sixcandoit.roomservice.service.event.EventService;
import com.sixcandoit.roomservice.service.office.OrganizationService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Name;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/event")
@Log4j2
public class EventController {

    private final OrganizationService organizationService;
    private final ModelMapper modelMapper;
    private final PageNationUtil pageNationUtil;
    private final EventService eventService;


    //이벤트 목록
    @GetMapping("/ev")
    public String event(Model model) {
        try {
            log.info("get에 들어옴");
            List<EventDTO> eventDTOS = eventService.list();
            model.addAttribute("eventDTOS", eventDTOS);


            return "/event/ev";
        } catch (Exception e) {
            throw new RuntimeException("이벤트 목록 맵핑 실패: " + e.getMessage());
        }
    }

    //이벤트 등록
    @PostMapping("/ev/register")
    @ResponseBody
    public ResponseEntity<String> EventRegister(@ModelAttribute EventDTO eventDTO) {

        try {

            eventService.register(eventDTO);
            System.out.println("저장성공");

            return ResponseEntity.ok("등록하였습니다.");

        } catch (Exception e) {
            System.out.println("저장실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록을 실패 하였습니다.");
        }
    }


    //포인트 삭제
    @GetMapping("/ev/delete")
    @ResponseBody
    public ResponseEntity<String> EventDelete(@RequestParam Integer idx) {
        // System.out.println("삭제");
        // System.out.println(idx);
        try {
            eventService.delete(idx);

            return ResponseEntity.ok("삭제하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제를 실패 하였습니다.");
        }

    }

    //포인트 수정
    @PostMapping("/ev/read")
    @ResponseBody
    public Map<String, Object> EventRead(@RequestParam Integer idx, Map map) {
        EventDTO eventDTO = eventService.read(idx);
        Map<String, Object> response = new HashMap<>();
        response.put("eventDTO", eventDTO);

        return response;
    }

    @PostMapping("/ev/update")
    @ResponseBody
    public ResponseEntity<String> EventUpdate(@ModelAttribute EventDTO eventDTO) {
        //System.out.println(eventDTO.toString());
        try {
            eventService.update(eventDTO);
            //System.out.println("수정성5공!!!");

            return ResponseEntity.ok("수정하였습니다.");
        } catch (Exception e) {
            //System.out.println("수정실패!!!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정을 실패 하였습니다.");
        }
    }

}


//CTRL_ALT_L : 자동 들여쓰기 정렬
//CTRL_D : 아래줄에 복사
//CTRL_Delete : 행삭제
//블럭
//CTRL_R : 바꾸기
//jsonBackReference: 무한 안걸리게 해주는 어노테이션
