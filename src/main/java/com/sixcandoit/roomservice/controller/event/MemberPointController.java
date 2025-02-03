package com.sixcandoit.roomservice.controller.event;

import com.sixcandoit.roomservice.dto.event.MemberPointDTO;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import com.sixcandoit.roomservice.service.event.MemberPointService;
import com.sixcandoit.roomservice.service.office.OrganizationService;
import com.sixcandoit.roomservice.util.PageNationUtil;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/event")
@Log4j2
public class MemberPointController {

    private final OrganizationService organizationService;
    private final ModelMapper modelMapper;
    private final PageNationUtil pageNationUtil;
    private final MemberPointService memberPointService;


    
    //포인트 목록
    @GetMapping("/memberpoint")
    public String memberPoint( Model model) {
        try {
            log.info("get에 들어옴");
            List<MemberPointDTO> memberPointDTOS = memberPointService.list();





            model.addAttribute("memberPointDTOS", memberPointDTOS);



            return "event/memberpoint";
        } catch (Exception e){
            throw new RuntimeException("포인트 목록 맵핑 실패: "+e.getMessage());
        }
    }

    //포인트 등록
    @PostMapping("/memberpoint/register")
    @ResponseBody
    public ResponseEntity<String> MemberPointRegister(@ModelAttribute MemberPointDTO memberPointDTO){
        try {

            memberPointService.register(memberPointDTO);
            System.out.println("저장성공");

            return  ResponseEntity.ok("등록하였습니다.");

        }catch (Exception e){
            System.out.println("저장실패");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록을 실패 하였습니다.");
        }
    }

    //포인트 삭제
    @GetMapping("/memberpoint/delete")
    @ResponseBody
    public ResponseEntity<String> MemberPointDelete(@RequestParam Integer idx){
       // System.out.println("삭제");
       // System.out.println(idx);
        try {
            memberPointService.delete(idx);

            return  ResponseEntity.ok("삭제하였습니다.");
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제를 실패 하였습니다.");
        }
    }

    //포인트 수정
    @GetMapping("/memberpoint/read")
    @ResponseBody
    public Map<String, Object> MemberPointRead(@RequestParam Integer idx, Map map){
        MemberPointDTO memberPointDTO = memberPointService.read(idx);
        Map<String,Object> response = new HashMap<>();
        response.put("memberPointDTO",memberPointDTO);

        return response;
    }

    @PostMapping("/memberpoint/update")
    @ResponseBody
    public ResponseEntity<String> MemberPointUpdate(@ModelAttribute MemberPointDTO memberPointDTO){
        //System.out.println(memberPointDTO.toString());
        try {
            memberPointService.update(memberPointDTO);
            //System.out.println("수정성5공!!!");

            return  ResponseEntity.ok("수정하였습니다.");
        }catch (Exception e){
            //System.out.println("수정실패!!!");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정을 실패 하였습니다.");
        }
    }




}


//CTRL_ALT_L : 자동 들여쓰기 정렬
//CTRL_D : 아래줄에 복사
//CTRL_Delete : 행삭제
//블럭
//CTRL_R : 바꾸기
//jsonBackReference: 무한 안걸리게 해주는 어노테이션
