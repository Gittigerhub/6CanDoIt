package com.sixcandoit.roomservice.controller.event;

import com.sixcandoit.roomservice.dto.event.MemberPointDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.service.event.MemberPointService;
import com.sixcandoit.roomservice.service.event.UserMemberPointService;
import com.sixcandoit.roomservice.service.member.MemberService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Controller
@RequestMapping("/event")

public class UserMemberPointController {

    private final MemberRepository memberRepository;
    private final UserMemberPointService userMemberPointService;

    //유저 맴버 리스트
    @GetMapping("/usermemberpoint")
    public String UserPointGet(@PageableDefault(page = 1) Pageable page,
                               @RequestParam(value = "type", defaultValue = "") String type,
                               @RequestParam(value = "keyword", defaultValue = "") String keyword,
                               @RequestParam(value = "startDate", defaultValue = "") LocalDateTime startDate,
                               @RequestParam(value = "endDate", defaultValue = "") LocalDateTime endDate,
                               Model model, Principal principal) {

        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(principal.getName());

        if (memberEntity.isEmpty()) {
            throw new RuntimeException("회원이 없습니다.");
        } else {
            System.out.println("컨트롤에 들어오는 타입:"+type);
            Page<MemberPointDTO> memberPointDTO = userMemberPointService.memberlist(memberEntity.get().getIdx(), page, type, keyword, startDate, endDate);
            Map<String, Integer> pageInfo = PageNationUtil.Pagination(memberPointDTO);
            model.addAttribute("memberPointDTO", memberPointDTO);
            model.addAllAttributes(pageInfo);
            model.addAttribute("type", type);
            model.addAttribute("keyword", keyword);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);


        }


        return "event/usermemberpoint";
    }


    //유저 맴버 읽기
    @GetMapping("/usermemberpoint/read")
    @ResponseBody
    public Map<String, Object> UserPointRead(@RequestParam Integer idx) {
        MemberPointDTO userPoint = userMemberPointService.read(idx);
        Map<String, Object> response = new HashMap<>();
        response.put("userPoint", userPoint);

        return response;
    }


}
