package com.sixcandoit.roomservice.controller;

import com.sixcandoit.roomservice.dto.AdvertisementDTO;
import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.service.AdvertisementService;
import com.sixcandoit.roomservice.service.ImageFileService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/advertisement")
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final PageNationUtil pageNationUtil;
    private final ImageFileService imageFileService;

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
        Map<String, Integer> pageInfo = pageNationUtil.Pagination(advertisementDTOS);

        model.addAllAttributes(pageInfo);                                        // 페이지 정보
        model.addAttribute("advertisementDTOS", advertisementDTOS);  // 데이터 전달
        model.addAttribute("type", type);                            //검색분류
        model.addAttribute("keyword", keyword);                      // 키워드

        return "advertisement/list";

    }

    // 모달 광고 등록하기
    @PostMapping("/register")
    @ResponseBody //HTTP 요청에 대한 응답을 JSON, XML, 텍스트 등의 형태로 반환
    public ResponseEntity<String> register(@ModelAttribute AdvertisementDTO advertisementDTO, Integer organidx,
                                           List<MultipartFile> imageFiles) {

        try {
            // 서비스에 등록 요청
            advertisementService.adRegister(advertisementDTO, organidx, imageFiles);

            // 등록 성공 시, HTTP에 상태 코드 200(OK)와 함께 응답을 보낸다.
            return ResponseEntity.ok("광고 등록이 성공적으로 되었습니다.");

        } catch (Exception e) {
            log.error("등록 중 오류발생", e);

            // 등록 실패 시, HTTP에 상태 코드 500과 함께 응답을 보낸다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("광고 등록을 실패하였습니다.");
        }

    }

    // 모달 광고 등록 업체 조회
    @PostMapping("/search/list")
    @ResponseBody //HTTP 요청에 대한 응답을 JSON, XML, 텍스트 등의 형태로 반환
    public ResponseEntity<List<OrganizationDTO>> searchOrgan(@RequestParam(value = "searchType", defaultValue = "") String searchType,
                                              @RequestParam(value = "searchWord", defaultValue = "") String searchWord) {

        try {
            // 서비스에 조회 요청
            List<OrganizationDTO> organizationDTO = advertisementService.searchOrgan(searchType, searchWord);

            // 등록 성공 시, HTTP에 상태 코드 200(OK)와 함께 응답을 보낸다.
            return ResponseEntity.ok(organizationDTO);

        } catch (Exception e) {
            log.error("등록 중 오류발생", e);

            // 등록 실패 시, HTTP에 상태 코드 500과 함께 응답을 보낸다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    // 광고 수정하기
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> Update(@ModelAttribute AdvertisementDTO advertisementDTO,
                                         List<MultipartFile> imageFiles) {

        // 이미지 조회전 join값 생성
        String join = "adver";

        try {
            // 서비스에 등록 요청
            advertisementService.adUpdate(advertisementDTO, join, imageFiles);

            // 수정 성공 시, HTTP에 상태 코드 200(OK)와 함께 응답을 보낸다.
            return ResponseEntity.ok("수정 하였습니다.");

        }catch (Exception e){

            // 수정 실패 시, HTTP에 상태 코드 500과 함께 응답을 보낸다.
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정을 실패하였습니다.");
        }
    }

    // 모달로 수정시 페이지 값 집어넣기
    @GetMapping("/update/read")
    @ResponseBody
    public Map<String, Object> updateRead(@RequestParam Integer idx, Map map){
        AdvertisementDTO advertisementDTO = advertisementService.adRead(idx);
        Map<String,Object> response = new HashMap<>();
        response.put("advertisementDTO",advertisementDTO);

        return response;
    }

    // 광고 상세보기
    @GetMapping("/read")
    public String read(@RequestParam Integer idx, Model model){

        // 이미지 조회전 join값 생성
        String join = "adver";

        // 광고 정보 서비스로 조회
        AdvertisementDTO advertisementDTO =
                advertisementService.adRead(idx);

        // 이미지 정보 서비스로 조회
        List<ImageFileDTO> imageFileDTOS =
                imageFileService.readImage(idx, join);

        // 대표이미지 존재여부 확인
        boolean hasRepImage = imageFileDTOS.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));


        // view로 전달
        model.addAttribute("advertisementDTO", advertisementDTO);
        model.addAttribute("imageFileDTOS", imageFileDTOS);
        model.addAttribute("hasRepImage", hasRepImage);

        return "advertisement/read";

    }

    // 광고 삭제하기
    @GetMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> MemberPointDelete(@RequestParam Integer idx){

        // 이미지 조회전 join값 생성
        String join = "adver";

        try {
            // idx로 데이터를 조회하여 삭제
            advertisementService.adDelete(idx, join);

            return  ResponseEntity.ok("삭제하였습니다.");
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제를 실패 하였습니다.");
        }
    }

}