package com.sixcandoit.roomservice.controller.event;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.event.EventDTO;
import com.sixcandoit.roomservice.entity.event.EventEntity;
import com.sixcandoit.roomservice.repository.event.EventRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.event.EventService;
import com.sixcandoit.roomservice.service.office.OrganizationService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private final ImageFileService imageFileService;
    private final EventRepository eventRepository;


    //이벤트 목록
    @GetMapping("/event")
    public String event(@PageableDefault(page = 1) Pageable page,
                        @RequestParam(value = "type", defaultValue = "") String type,
                        @RequestParam(value = "keyword", defaultValue = "") String keyword,
                        @RequestParam(value = "startDate", defaultValue = "") LocalDateTime startDate,
                        @RequestParam(value = "endDate", defaultValue = "") LocalDateTime endDate,
                        Model model) {


        System.out.println("컨트롤에 들어오는 타입:" + type);
        Page<EventDTO> eventDTOS = eventService.list(keyword, type, startDate, endDate, page);
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(eventDTOS);
        model.addAttribute("eventDTOS", eventDTOS);
        model.addAllAttributes(pageInfo);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);


        return "event/event";
    }


    /*
    //이벤트 등록
    @PostMapping("/event/register")
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
     */

    //이벤트 등록
    @GetMapping("/eventregister")
    public String eventRegisterGet() {


        return "event/eventregister";
    }

    @PostMapping("/eventregister")
    public String eventRegisterPost(@Valid @ModelAttribute EventDTO eventDTO, Model model,
                                    BindingResult bindingResult, List<MultipartFile> Files) {


        if (bindingResult.hasErrors()) {
            System.out.println("유효성 검사 중 오류");
            bindingResult.getAllErrors().forEach(error -> log.error(error.getDefaultMessage()));
            return "event/eventregister";

        }
        EventEntity eventEntity = modelMapper.map(eventDTO, EventEntity.class);

        //등록 하기
        eventService.register(eventDTO,Files);


        return "redirect:/event/event";
    }


    //이벤트 삭제
    @PostMapping("/event/delete")
    @ResponseBody
    public ResponseEntity<String> EventDelete(@RequestParam Integer idx) {
        System.out.println("삭제");
        System.out.println(idx);
        try {
            eventService.delete(idx);

            return ResponseEntity.ok("삭제하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제를 실패 하였습니다.");
        }

    }

    /*
    //이벤트 자세히 보기
    @PostMapping("/event/read")
    @ResponseBody
    public Map<String, Object> eventRead(@RequestParam Integer idx) {
        EventDTO eventDTO = eventService.read(idx);
        Map<String, Object> response = new HashMap<>();
        response.put("eventDTO", eventDTO);


        return response;
    }
     */
    @GetMapping("/eventread")
    public String read(Integer idx, Model model) {

        EventDTO eventDTORead = eventService.read(idx);
        List<ImageFileDTO> imageFileDTOList = imageFileService.readImage(idx, "event");
        boolean hasRepImage = imageFileDTOList.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));


        model.addAttribute("eventDTORead", eventDTORead);
        model.addAttribute("imageFileDTOList", imageFileDTOList);
        model.addAttribute("hasRepImage", hasRepImage);

        return "event/eventread";
    }

    /*
    //이벤트 수정
    @PostMapping("/event/update")
    @ResponseBody
    public ResponseEntity<String> EventUpdate(@ModelAttribute EventDTO eventDTO, List<MultipartFile> Files) {
        //System.out.println(eventDTO.toString());
        try {
            eventService.update(eventDTO, Files);
            //System.out.println("수정성5공!!!");

            return ResponseEntity.ok("수정을 성공하였습니다.");
        } catch (Exception e) {
            //System.out.println("수정실패!!!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정을 실패 하였습니다.");
        }
    }
     */

    @GetMapping("/eventupdate")
    public String eventUpdateGet(Model model, Integer idx) {
        EventDTO eventRead = eventService.read(idx);
        if (eventRead != null) {
            List<ImageFileDTO> imageFileDTOList = imageFileService.readImage(idx, "event");
            boolean hasRepImage = imageFileDTOList.stream()
                    .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));


            model.addAttribute("eventRead", eventRead);
            model.addAttribute("imageFileDTOList", imageFileDTOList);
            model.addAttribute("hasRepImage", hasRepImage);
        } else {
            System.out.println("잘못된 조회입니다.");
        }


        return "event/eventupdate";
    }

    @PostMapping("/eventupdate")
    public String eventUpdatePost(@ModelAttribute EventDTO eventDTO, List<MultipartFile> Files, Model model) {
        try {


            eventService.update(eventDTO, Files);
        } catch (Exception e) {
            System.out.println("수정 오류:" + e.getMessage());
        }


        return "redirect:/event/event";
    }


}


//CTRL_ALT_L : 자동 들여쓰기 정렬
//CTRL_D : 아래줄에 복사
//CTRL_Delete : 행삭제
//블럭
//CTRL_R : 바꾸기
//jsonBackReference: 무한 안걸리게 해주는 어노테이션
