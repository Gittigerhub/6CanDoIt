package com.sixcandoit.roomservice.controller.office;

import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.service.office.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/office")
@Log4j2
public class OfficeController {

    private final OrganizationService organizationService;
    private final ModelMapper modelMapper;


    @GetMapping("/list")
    public String list(OrganizationDTO organizationDTO) {

        return "office/officelist";

    }

    @PostMapping("/organ/register")
    @ResponseBody //HTTP 요청에 대한 응답을 JSON, XML, 텍스트 등의 형태로 반환
    public ResponseEntity<String> registerProc(@ModelAttribute OrganizationDTO organizationDTO) {

        try {
            // 서비스에 등록 요청
            organizationService.organRegister(organizationDTO);

            // 등록 성공 시, HTTP에 상태 코드 200(OK)와 함께 응답을 보낸다.
            return ResponseEntity.ok("등록 하였습니다.");
        } catch (Exception e) {
            log.error("등록 중 오류발생", e);

            // 등록 실패 시, HTTP에 상태 코드 500과 함께 응답을 보낸다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장을 실패하였습니다.");
        }

    }

    @GetMapping("/organ")
    public String organDetail(Integer idx, Model model) {

        // 서비스로 조회
        OrganizationDTO organDTO =
            organizationService.organRead(idx);

        // view로 전달
        model.addAttribute("organDTO", organDTO);

        return "office/organdetail";

    }

    @GetMapping("/shop")
    public String shopDetail() {

        return "office/shopdetail";

    }

}