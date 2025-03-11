package com.sixcandoit.roomservice.controller.event;

import com.sixcandoit.roomservice.dto.event.EventDTO;
import com.sixcandoit.roomservice.dto.event.MemberPointDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.event.EventRepository;
import com.sixcandoit.roomservice.service.event.EventService;
import com.sixcandoit.roomservice.service.event.UserEventService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/event")
public class UserEventController {

    private final UserEventService userEventService;
    private final ModelMapper modelMapper;
    private final EventService eventService;
    private final EventRepository eventRepository;


    //유저 맴버 리스트
    @GetMapping("/userevent")
    public String userEvent(@PageableDefault(page = 1) Pageable page,
                            @RequestParam(value = "type", defaultValue = "") String type,
                            @RequestParam(value = "keyword", defaultValue = "") String keyword,
                            @RequestParam(value = "startDate", defaultValue = "") LocalDateTime startDate,
                            @RequestParam(value = "endDate", defaultValue = "") LocalDateTime endDate,
                            Model model) {



            System.out.println("컨트롤에 들어오는 타입:"+type);
            Page<EventDTO> userEventDTO = userEventService.list(keyword, type, startDate, endDate,page);
            Map<String, Integer> pageInfo = PageNationUtil.Pagination(userEventDTO);
            model.addAttribute("userEventDTO", userEventDTO);
            model.addAllAttributes(pageInfo);
            model.addAttribute("type", type);
            model.addAttribute("keyword", keyword);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);





        return "event/userevent";
    }

    @GetMapping("/usereventread")
    public String userEventRead(Integer idx,Model model) {

        EventDTO eventDTORead = eventService.read(idx);

        model.addAttribute("eventDTORead", eventDTORead);

        return "event/usereventread";
    }


}
