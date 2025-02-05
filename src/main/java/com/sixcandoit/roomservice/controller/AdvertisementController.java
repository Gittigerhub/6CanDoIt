package com.sixcandoit.roomservice.controller;

import com.sixcandoit.roomservice.dto.AdvertisementDTO;
import com.sixcandoit.roomservice.service.AdvertisementService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/advertisement")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @GetMapping("/test")
    public String aaa(){

        return "advertisement/list";
    }

    // 목록
    @GetMapping("/list")
    public String list(@PageableDefault(page = 1) Pageable page,                            // 페이지 정보
                       @RequestParam(value = "type", defaultValue = "") String type,        // 검색대상
                       @RequestParam(value = "keyword", defaultValue = "") String keyword,  // 키워드
                       Model model){

        // 해당페이지의 내용을 서비스를 통해 데이터베이스로부터 조회
        Page<AdvertisementDTO> advertisementDTOS =
                advertisementService.adList(page, type, keyword);

        // 조회결과를 이용한 페이지 처리
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(advertisementDTOS);

        model.addAllAttributes(pageInfo);                                        // 페이지 정보
        model.addAttribute("advertisementDTOS", advertisementDTOS);  // 데이터 전달
        model.addAttribute("type", type);                            //검색분류
        model.addAttribute("keyword", keyword);                      // 키워드

        return "advertisement/list";

    }

    // 모달 광고 등록하기
    @PostMapping("/register")
    @ResponseBody //HTTP 요청에 대한 응답을 JSON, XML, 텍스트 등의 형태로 반환
    public ResponseEntity<String> register(@ModelAttribute AdvertisementDTO advertisementDTO) {

        try {
            // 서비스에 등록 요청
            advertisementService.adRegister(advertisementDTO);

            // 등록 성공 시, HTTP에 상태 코드 200(OK)와 함께 응답을 보낸다.
            return ResponseEntity.ok("광고 등록이 성공적으로 되었습니다.");

        } catch (Exception e) {
            log.error("등록 중 오류발생", e);

            // 등록 실패 시, HTTP에 상태 코드 500과 함께 응답을 보낸다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("광고 등록을 실패하였습니다.");
        }

    }

    // 광고 수정하기
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> ShopDetailUpdate(@ModelAttribute AdvertisementDTO advertisementDTO){

        try {
            // 서비스에 수정 요청
            advertisementService.adUpdate(advertisementDTO);

            // 수정 성공 시, HTTP에 상태 코드 200(OK)와 함께 응답을 보낸다.
            return  ResponseEntity.ok("수정하였습니다.");

        }catch (Exception e){

            // 수정 실패 시, HTTP에 상태 코드 500과 함께 응답을 보낸다.
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정을 실패 하였습니다.");
        }
    }

    // 광고 상세보기
    @GetMapping("/read")
    public String read(@RequestParam Integer idx, Model model){

        // idx로 데이터 조회
        log.info("개별 데이터를 읽는 중입니다.");
        AdvertisementDTO advertisementDTO = advertisementService.adRead(idx);

        log.info("개별 데이터를 페이지에 전달하는 중입니다.");
        model.addAttribute("advertisementDTO", advertisementDTO);

        return "advertisement/read";
    }

    // 광고 삭제하기
    @PostMapping("/delete")
    public String delete(Integer idx){

        log.info("데이터를 삭제합니다.");

        // idx로 조회한 데이터 삭제
        advertisementService.adDelete(idx);

        return "redirect:/advertisement/list";
    }

}