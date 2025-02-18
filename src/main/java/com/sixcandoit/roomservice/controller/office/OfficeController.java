package com.sixcandoit.roomservice.controller.office;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.dto.office.ShopDetailDTO;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.office.OrganizationService;
import com.sixcandoit.roomservice.service.office.ShopDetailService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/office")
@Log4j2
public class OfficeController {

    private final OrganizationService organizationService;
    private final ModelMapper modelMapper;
    private final PageNationUtil pageNationUtil;
    private final ShopDetailService shopDetailService;
    private final ImageFileService imageFileService;


    @GetMapping("/list")
    public String list(@PageableDefault(page=1) Pageable page,
                       @RequestParam(value="keyword", defaultValue = "") String keyword,
                       @RequestParam(value="type", defaultValue = "") String type, Model model) {

        // 서비스에 조회 요청
        Page<OrganizationDTO> organDTO = organizationService.organList(page, type, keyword);

        // 조회결과를 이용한 페이지 처리
        Map<String,Integer> pageInfo = pageNationUtil.Pagination(organDTO);

        // 페이지 정보, 조회내용, 검색어, 조직 타입을 전달
        model.addAllAttributes(pageInfo);
        model.addAttribute("organDTO", organDTO);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);

        return "office/officelist";

    }

    @PostMapping("/organ/register")
    @ResponseBody //HTTP 요청에 대한 응답을 JSON, XML, 텍스트 등의 형태로 반환
    public ResponseEntity<String> register(@ModelAttribute OrganizationDTO organizationDTO, List<MultipartFile> imageFiles) {

        try {
            // 서비스에 등록 요청
            organizationService.organRegister(organizationDTO, imageFiles);

            // 등록 성공 시, HTTP에 상태 코드 200(OK)와 함께 응답을 보낸다.
            return ResponseEntity.ok("등록 하였습니다.");
        } catch (Exception e) {
            log.error("등록 중 오류발생", e);

            // 등록 실패 시, HTTP에 상태 코드 500과 함께 응답을 보낸다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장을 실패하였습니다.");
        }

    }

    // 모달로 수정시 페이지 값 집어넣기
    @GetMapping("/organ/read")
    @ResponseBody
    public Map<String, Object> MemberPointRead(@RequestParam Integer idx, Map map){
        OrganizationDTO organizationDTO = organizationService.organRead(idx);
        Map<String,Object> response = new HashMap<>();
        response.put("organizationDTO",organizationDTO);

        return response;
    }

    // 모달로 수정 진행
    @PostMapping("/organ/update")
    @ResponseBody //HTTP 요청에 대한 응답을 JSON, XML, 텍스트 등의 형태로 반환
    public ResponseEntity<String> update(@ModelAttribute OrganizationDTO organizationDTO, String join, List<MultipartFile> imageFiles) {

        try {
            // 서비스에 등록 요청
            organizationService.organUpdate(organizationDTO, join, imageFiles);

            // 등록 성공 시, HTTP에 상태 코드 200(OK)와 함께 응답을 보낸다.
            return ResponseEntity.ok("수정 하였습니다.");
        } catch (Exception e) {
            log.error("수정 중 오류발생", e);

            // 등록 실패 시, HTTP에 상태 코드 500과 함께 응답을 보낸다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정을 실패하였습니다.");
        }

    }

    @GetMapping("/organ")
    public String organDetail(Integer idx, Model model) {

        // 이미지 조회전 join값 생성
        String join = "organ";

        // 조직 정보 서비스로 조회
        OrganizationDTO organDTO =
            organizationService.organRead(idx);

        // 이미지 정보 서비스로 조회
        List<ImageFileDTO> imageFileDTOS =
                imageFileService.readImage(idx, join);

        // 대표이미지 존재여부 확인
        boolean hasRepImage = imageFileDTOS.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

        // view로 전달
        model.addAttribute("organDTO", organDTO);
        model.addAttribute("imageFileDTOS", imageFileDTOS);
        model.addAttribute("hasRepImage", hasRepImage);

        return "office/organdetail";

    }

    // 조직 삭제
    @GetMapping("/organ/delete")
    @ResponseBody
    public ResponseEntity<String> MemberPointDelete(@RequestParam Integer idx){

        // 이미지 조회전 join값 생성
        String join = "organ";

        try {
            // idx로 데이터를 조회하여 삭제
            organizationService.organDelete(idx, join);

            return  ResponseEntity.ok("삭제하였습니다.");
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제를 실패 하였습니다.");
        }
    }

    //상점 목록
    @GetMapping("/shopdetail")
    public String shopDetail(Model model) {
        try {
            log.info("get에 들어옴");
            List<ShopDetailDTO> shopDetailDTOS = shopDetailService.list();

            model.addAttribute("shopDetailDTOS", shopDetailDTOS);

            return "office/shopdetail";
        } catch (Exception e){
            throw new RuntimeException("상점 목록 맵핑 실패: "+e.getMessage());
        }
    }

    //상점 등록
    @PostMapping("/shopdetail/register")
    @ResponseBody
    public ResponseEntity<String> ShopDetailRegister(@ModelAttribute ShopDetailDTO shopDetailDTO){
        try {
            shopDetailService.register(shopDetailDTO);
            System.out.println("저장성공");
            return  ResponseEntity.ok("등록하였습니다.");
        }catch (Exception e){
            System.out.println("저장실패");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록을 실패 하였습니다.");
        }
    }

    //상점 삭제
    @GetMapping("/shopdetail/delete")
    @ResponseBody
    public ResponseEntity<String> ShopDetailDelete(@RequestParam Integer idx){

        try {
            shopDetailService.delete(idx);

            return  ResponseEntity.ok("삭제하였습니다.");
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제를 실패 하였습니다.");
        }
    }

    //상점 수정
    @GetMapping("/shopdetail/read")
    @ResponseBody
    public Map<String, Object> ShopDetailRead(@RequestParam Integer idx, Map map){
        ShopDetailDTO shopDetailDTO = shopDetailService.read(idx);
        Map<String,Object> response = new HashMap<>();
        response.put("shopDetailDTO",shopDetailDTO);

        return response;
    }

    @PostMapping("/shopdetail/update")
    @ResponseBody
    public ResponseEntity<String> ShopDetailUpdate(@ModelAttribute ShopDetailDTO shopDetailDTO){
        //System.out.println(shopDetailDTO.toString());
        try {
            shopDetailService.update(shopDetailDTO);
            //System.out.println("수정성5공!!!");
            return  ResponseEntity.ok("수정하였습니다.");
        }catch (Exception e){
            //System.out.println("수정실패!!!");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정을 실패 하였습니다.");
        }
    }

    @GetMapping("/shopdetail/realread")
    public String shopdetailRealread(Integer idx, Model model) {

        // 조직 정보 서비스로 조회
        OrganizationDTO organDTO =
                organizationService.organRead(idx);

        // view로 전달
        model.addAttribute("organDTO", organDTO);

        return "office/shopread";

    }

}


//CTRL_ALT_L : 자동 들여쓰기 정렬
//CTRL_D : 아래줄에 복사
//CTRL_Delete : 행삭제
//블럭
//CTRL_R : 바꾸기
//jsonBackReference: 무한 안걸리게 해주는 어노테이션
