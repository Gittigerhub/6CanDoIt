package com.sixcandoit.roomservice.controller.event;

import com.sixcandoit.roomservice.dto.event.MemberPointDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.service.event.MemberPointService;
import com.sixcandoit.roomservice.service.event.UserMemberPointService;
import com.sixcandoit.roomservice.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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


    @GetMapping("/usermemberpoint")
    public String UserPointGet(Model model, Principal principal) {

        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(principal.getName());

        if (memberEntity.isEmpty()) {
            throw new RuntimeException("회원이 없습니다.");
        } else {
            List<MemberPointDTO> memberPointDTO = userMemberPointService.memberlist(memberEntity.get().getIdx());
            MemberPointDTO memberName = userMemberPointService.read(memberEntity.get().getIdx());

            model.addAttribute("memberPointDTO", memberPointDTO);
            model.addAttribute("memberName", memberName);
        }


        return "/event/usermemberpoint";
    }

    @GetMapping("/usermemberpoint/read")
    @ResponseBody
    public Map<String, Object> UserPointRead(@RequestParam Integer idx, Map map) {
        MemberPointDTO userPoint = userMemberPointService.read(idx);
        Map<String, Object> response = new HashMap<>();
        response.put("userPoint", userPoint);

        return response;
    }


}
