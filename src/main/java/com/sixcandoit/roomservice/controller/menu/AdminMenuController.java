package com.sixcandoit.roomservice.controller.menu;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.Menu.MenuDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.admin.AdminService;
import com.sixcandoit.roomservice.service.menu.MenuService;
import com.sixcandoit.roomservice.service.office.OrganizationService;
import com.sixcandoit.roomservice.service.office.ShopDetailService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin")
public class AdminMenuController {
    //HTML 전달할 S3정보
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;


    private final ModelMapper modelMapper;
    private final MenuService menuService;
    private final ImageFileService imageFileService;
    private final ShopDetailService shopDetailService;
    private final AdminService adminService;
    private final OrganizationService organizationService;

    //아이템 등록
    @GetMapping("/menu/ho/registermenu")
    public String registerHOMenu(Model model, Principal principal) {

        //로그인이 안되어 있으면, 접근 불가능하도록
        if (principal == null) {
            return "redirect:/login";
        }

        System.out.println("이메일 : " + principal.getName());
        try {


        } catch (Exception e) {
            log.error("관리자 정보 조회 중 오류 발생: ", e);

        }

        return "menu/horegistermenu";
    }

    @GetMapping("/menu/bo/registermenu")
    public String registerBOMenu(Model model, Principal principal) {

        //로그인이 안되어 있으면, 접근 불가능하도록
        if (principal == null) {
            return "redirect:/login";
        }

        System.out.println("이메일 : " + principal.getName());
        try {


        } catch (Exception e) {
            log.error("관리자 정보 조회 중 오류 발생: ", e);

        }

        return "menu/boregistermenu";
    }

    //아이템 등록
    @PostMapping("/menu/hop/registermenu")
    public String registerHOMenuPost(@Valid MenuDTO menuDTO, BindingResult bindingResult,
                                   List<MultipartFile> imageFiles,
                                   Model model, Principal principal) {
        //들어오는 값을 확인
        System.out.println("menuDTO : " + menuDTO.toString());
        System.out.println("principal : " + principal.getName());

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 에러");
            log.info(bindingResult.getAllErrors());     //확인된 모든 에러 콘솔창 출력

            return "menu/horegistermenu";  //다시 이전 페이지
        }

        try {

            // 등록 서비스
            menuService.menuRegister(menuDTO, imageFiles, principal.getName());

            System.out.println("상품등록 완료!");

            return "redirect:/admin/menu/ho/adlistmenu";

        }catch (Exception e) {
            e.printStackTrace();
            log.info("파일 등록시 문제가 발생했습니다.");
            model.addAttribute("msg", "올바른 파일을 등록하세요.");
            return "menu/horegistermenu";  //다시 이전 페이지로
        }
    }

    //아이템 등록
    @PostMapping("/menu/bop/registermenu")
    public String registerBOMenuPost(@Valid MenuDTO menuDTO, BindingResult bindingResult,
                                   List<MultipartFile> imageFiles,
                                   Model model, Principal principal) {
        //들어오는 값을 확인
        System.out.println("menuDTO : " + menuDTO.toString());
        System.out.println("principal : " + principal.getName());

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 에러");
            log.info(bindingResult.getAllErrors());     //확인된 모든 에러 콘솔창 출력

            return "menu/horegistermenu";  //다시 이전 페이지
        }

        try {

            // 등록 서비스
            menuService.menuRegister(menuDTO, imageFiles, principal.getName());

            System.out.println("상품등록 완료!");

            return "redirect:/admin/menu/bo/adlistmenu";

        }catch (Exception e) {
            e.printStackTrace();
            log.info("파일 등록시 문제가 발생했습니다.");
            model.addAttribute("msg", "올바른 파일을 등록하세요.");
            return "menu/boregistermenu";  //다시 이전 페이지로
        }
    }

    //메뉴 목록 확인
    @GetMapping("/menu/ho/adreadmenu")
    public String readHOMenu(@RequestParam(required = false) Integer menuidx,
                           @RequestParam(required = false) Integer organIdx,
                           Model model, RedirectAttributes redirectAttributes) {

        // menuidx가 null인 경우 처리
        if (menuidx == null) {
            redirectAttributes.addFlashAttribute("msg", "메뉴 ID가 없습니다.");
            return "redirect:/admin/menu/ho/adlistmenu";
        }
        String join = "menu";

        try {
            // 메뉴 정보를 읽어온다.
            MenuDTO menuDTO = menuService.menuRead(menuidx);

            // 이미지 정보를 읽어온다.
            List<ImageFileDTO> imageFileDTOList = imageFileService.readImage(menuidx, join);

            if (menuDTO == null) {  // menuService에서 null이 반환되는 경우 체크
                throw new EntityNotFoundException("존재하지 않는 메뉴입니다.");
            }

            // 대표이미지 존재여부 확인
            boolean hasRepImage = imageFileDTOList.stream()
                    .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

            //메뉴 불러오기
            log.info("menuDTO: " + menuDTO);
            model.addAttribute("menuDTO", menuDTO);
            model.addAttribute("organIdx", organIdx);
            model.addAttribute("bucket", bucket);
            model.addAttribute("region", region);
            model.addAttribute("folder", folder);
            model.addAttribute("imageFileDTOList", imageFileDTOList);
            model.addAttribute("hasRepImage", hasRepImage);

            return "menu/hoadreadmenu";
        }catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 메뉴입니다.");
            return "redirect:/admin/menu/ho/adreadmenu";
        } catch (Exception e) {
            // 예기치 않은 예외 처리 (디버깅용 로그 등 추가 가능)
            redirectAttributes.addFlashAttribute("msg", "알 수 없는 오류가 발생했습니다.");
            return "redirect:/admin/menu/ho/adreadmenu";
        }
    }

    //메뉴 목록 확인
    @GetMapping("/menu/bo/adreadmenu")
    public String readBOMenu(@RequestParam(required = false) Integer menuidx,
                           @RequestParam(required = false) Integer organIdx,
                           Model model, RedirectAttributes redirectAttributes) {

        // menuidx가 null인 경우 처리
        if (menuidx == null) {
            redirectAttributes.addFlashAttribute("msg", "메뉴 ID가 없습니다.");
            return "redirect:/admin/menu/bo/adlistmenu";
        }
        String join = "menu";

        try {
            // 메뉴 정보를 읽어온다.
            MenuDTO menuDTO = menuService.menuRead(menuidx);

            // 이미지 정보를 읽어온다.
            List<ImageFileDTO> imageFileDTOList = imageFileService.readImage(menuidx, join);

            if (menuDTO == null) {  // menuService에서 null이 반환되는 경우 체크
                throw new EntityNotFoundException("존재하지 않는 메뉴입니다.");
            }

            // 대표이미지 존재여부 확인
            boolean hasRepImage = imageFileDTOList.stream()
                    .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

            //메뉴 불러오기
            log.info("menuDTO: " + menuDTO);
            model.addAttribute("menuDTO", menuDTO);
            model.addAttribute("organIdx", organIdx);
            model.addAttribute("bucket", bucket);
            model.addAttribute("region", region);
            model.addAttribute("folder", folder);
            model.addAttribute("imageFileDTOList", imageFileDTOList);
            model.addAttribute("hasRepImage", hasRepImage);

            return "menu/hoadreadmenu";
        }catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 메뉴입니다.");
            return "redirect:/admin/menu/bo/adreadmenu";
        } catch (Exception e) {
            // 예기치 않은 예외 처리 (디버깅용 로그 등 추가 가능)
            redirectAttributes.addFlashAttribute("msg", "알 수 없는 오류가 발생했습니다.");
            return "redirect:/admin/menu/bo/adreadmenu";
        }
    }

    //메뉴 목록 확인　｀
    @GetMapping("/menu/ho/adlistmenu")
    public String listHOMenu(@PageableDefault(page = 1) Pageable page, //페이지 정보
                           @RequestParam(value = "type", defaultValue = "") String type, //검색 대상
                           @RequestParam(value = "keyword", defaultValue = "") String keyword, //키워드
                           Model model, Principal principal) {

        // 이미지 구별
        String join = "menu";

        // 이메일
        String email = principal.getName();

        // 회원찾기
        AdminEntity admin = adminService.findByAdminEmail(email);

        // 호텔찾기
        OrganizationEntity organization = organizationService.findById(admin.getOrganizationJoin().getIdx())
                .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));

        // 호텔의 idx
        Integer organIdx = organization.getIdx();

        //해당페이지의 내용을 서비스를 통해 데이터베이스로부터 조회
        Page<MenuDTO> menuDTOS = menuService.menuList(page, type, keyword, organIdx);

        //html에 필요한 페이지 정보를 받기
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(menuDTOS);

        // DTO들 리스트로 가져오기
        List<MenuDTO> menu = menuDTOS.getContent();

        // 이미지 데이터를 담을 Map 생성 (menu의 idx를 key로 저장)
        Map<Integer, List<ImageFileDTO>> imageFileMap = new HashMap<>();
        Map<Integer, Boolean> repImageMap = new HashMap<>();

        for (MenuDTO menuDTO : menu) {
            // 이미지 조회
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(menuDTO.getIdx(), join);

            // Map에 저장 (menu의 idx를 key로 함)
            imageFileMap.put(menuDTO.getIdx(), imageFileDTOS);

            // 대표 사진 여부 확인 후 저장
            boolean hasRepImage = imageFileDTOS.stream()
                    .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

            repImageMap.put(menuDTO.getIdx(), hasRepImage);
        }

        // 모델에 추가
        model.addAttribute("menu", menu);  // 메뉴 리스트
        model.addAttribute("imageFileMap", imageFileMap); // 메뉴별 이미지 리스트
        model.addAttribute("repImageMap", repImageMap); // 메뉴별 대표 사진 여부
        model.addAttribute("menulist", menuDTOS); //데이터 전달
        model.addAllAttributes(pageInfo);   //페이지 정보
        model.addAttribute("type", type);   //검색 분류
        model.addAttribute("keyword", keyword); //키워드


        return "menu/hoadlistmenu";

    }

    //메뉴 목록 확인　｀
    @GetMapping("/menu/bo/adlistmenu")
    public String listBOMenu(@PageableDefault(page = 1) Pageable page, //페이지 정보
                           @RequestParam(value = "type", defaultValue = "") String type, //검색 대상
                           @RequestParam(value = "keyword", defaultValue = "") String keyword, //키워드
                           Model model, Principal principal) {

        // 이미지 구별
        String join = "menu";

        // 이메일
        String email = principal.getName();

        // 회원찾기
        AdminEntity admin = adminService.findByAdminEmail(email);

        // 호텔찾기
        OrganizationEntity organization = organizationService.findById(admin.getOrganizationJoin().getIdx())
                .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));

        // 호텔의 idx
        Integer organIdx = organization.getIdx();

        //해당페이지의 내용을 서비스를 통해 데이터베이스로부터 조회
        Page<MenuDTO> menuDTOS = menuService.menuList(page, type, keyword, organIdx);

        //html에 필요한 페이지 정보를 받기
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(menuDTOS);

        // DTO들 리스트로 가져오기
        List<MenuDTO> menu = menuDTOS.getContent();

        // 이미지 데이터를 담을 Map 생성 (menu의 idx를 key로 저장)
        Map<Integer, List<ImageFileDTO>> imageFileMap = new HashMap<>();
        Map<Integer, Boolean> repImageMap = new HashMap<>();

        for (MenuDTO menuDTO : menu) {
            // 이미지 조회
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(menuDTO.getIdx(), join);

            // Map에 저장 (menu의 idx를 key로 함)
            imageFileMap.put(menuDTO.getIdx(), imageFileDTOS);

            // 대표 사진 여부 확인 후 저장
            boolean hasRepImage = imageFileDTOS.stream()
                    .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

            repImageMap.put(menuDTO.getIdx(), hasRepImage);
        }

        // 모델에 추가
        model.addAllAttributes(pageInfo);                               // 페이지 정보
        model.addAttribute("menulist", menuDTOS);           // 데이터 전달
        model.addAttribute("menu", menu);                   // 메뉴 리스트
        model.addAttribute("imageFileMap", imageFileMap);   // 메뉴별 이미지 리스트
        model.addAttribute("repImageMap", repImageMap);     // 메뉴별 대표 사진 여부

        return "menu/boadlistmenu";

    }

    //Ajax 요청에 의해 카테고리별 메뉴목록을 반환하는 메소드
    @GetMapping("/menu/ajax/adlistmenu")
    public String ajaxListMenu(@RequestParam(value = "category", defaultValue = "ALL") String category,
                               @PageableDefault(page = 1) Pageable page,
                               Model model, Principal principal) {

        // 로그인한 회원정보로 예약건 찾아서 해당 호텔정보 가져오기
        OrganizationDTO organizationDTO = menuService.findMemberRes(principal.getName());

        // 호텔의 idx
        Integer organIdx = organizationDTO.getIdx();

        // 해당 페이지의 내용을 서비스를 통해 데이터베이스로부터 조회
        Page<MenuDTO> menuDTOS = menuService.selectCate(page, category, organIdx);

        // DTO들 리스트로 가져오기
        List<MenuDTO> menu = menuDTOS.getContent();

        // 이미지 데이터를 담을 Map 생성 (menu의 idx를 key로 저장)
        Map<Integer, List<ImageFileDTO>> imageFileMap = new HashMap<>();
        Map<Integer, Boolean> repImageMap = new HashMap<>();

        for (MenuDTO menuDTO : menu) {
            // 이미지 조회
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(menuDTO.getIdx(), "menu");

            // Map에 저장 (menu의 idx를 key로 함)
            imageFileMap.put(menuDTO.getIdx(), imageFileDTOS);

            // 대표 사진 여부 확인 후 저장
            boolean hasRepImage = imageFileDTOS.stream()
                    .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

            repImageMap.put(menuDTO.getIdx(), hasRepImage);
        }

        // 모델에 추가
        model.addAttribute("menu", menu);                   // 메뉴 리스트
        model.addAttribute("imageFileMap", imageFileMap);   // 메뉴별 이미지 리스트
        model.addAttribute("repImageMap", repImageMap);     // 메뉴별 대표 사진 여부
        model.addAttribute("menulist", menuDTOS);           // page 데이터 전달

        return "menu/adlistmenu :: menulistfragment";
    }

    @GetMapping("/menu/ho/updatemenu")
    public String updateHOMenu(@RequestParam("idx") Integer menuidx, Model model, Principal principal ) {

        String join = "menu";

        // menuidx가 null인 경우 처리
        if (menuidx == null) {
            return "redirect:/admin/menu/ho/adlistmenu";
        }

        //메뉴 정보 조회
        MenuDTO menuDTO = menuService.menuRead(menuidx);

        // 이미지 정보를 읽어온다.
        List<ImageFileDTO> imageFileDTOList = imageFileService.readImage(menuidx, join);

        // 대표이미지 존재여부 확인
        boolean hasRepImage = imageFileDTOList.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

        System.out.println(hasRepImage);
        System.out.println(hasRepImage);
        System.out.println(hasRepImage);

        //메뉴 정보가 있다면 수정페이지로 이동
        if (menuDTO != null) {
            model.addAttribute("menuDTO", menuDTO);
            model.addAttribute("imageFileDTOList", imageFileDTOList);
            model.addAttribute("hasRepImage", hasRepImage);
            return "menu/houpdatemenu";//수정페이지로 이동
        }else {
            return "redirect:/admin/menu/ho/adlistmenu";
        }
    }

    @GetMapping("/menu/bo/updatemenu")
    public String updateBOMenu(@RequestParam("idx") Integer menuidx, Model model, Principal principal ) {

        String join = "menu";

        // menuidx가 null인 경우 처리
        if (menuidx == null) {
            return "redirect:/admin/menu/bo/adlistmenu";
        }

        //메뉴 정보 조회
        MenuDTO menuDTO = menuService.menuRead(menuidx);

        // 이미지 정보를 읽어온다.
        List<ImageFileDTO> imageFileDTOList = imageFileService.readImage(menuidx, join);

        // 대표이미지 존재여부 확인
        boolean hasRepImage = imageFileDTOList.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

        //메뉴 정보가 있다면 수정페이지로 이동
        if (menuDTO != null) {
            model.addAttribute("menuDTO", menuDTO);
            model.addAttribute("imageFileDTOList", imageFileDTOList);
            model.addAttribute("hasRepImage", hasRepImage);
            return "menu/boupdatemenu";//수정페이지로 이동
        }else {
            return "redirect:/admin/menu/bo/adlistmenu";
        }
    }

    @PostMapping("/menu/ho/updatemenu")
    public String updateHOMenuPost(@Valid MenuDTO menuDTO, BindingResult bindingResult,
                                 List<MultipartFile> multipartFile, Model model) {

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 에러");
            log.info(bindingResult.getAllErrors()); //확인된 모든 에러 콘솔창 출력

            return "menu/houpdatemenu";  //다시 이전페이지로
        }

        try {
            // 새로운 이미지들 저장 처리
            String join = "menu";

            // 업데이트 서비스
            menuService.menuUpdate(menuDTO, join, multipartFile);

        } catch (Exception e) {
            log.error("메뉴 업데이트 오류: ", e);
            // 오류 발생 시 모델에 에러 메시지를 추가하거나 다시 페이지로 이동
            model.addAttribute("msg", "메뉴 업데이트 중 오류가 발생했습니다.");
            return "menu/houpdatemenu";
        }

        return "redirect:/admin/menu/ho/adlistmenu";

    }

    @PostMapping("/menu/bo/updatemenu")
    public String updateBOMenuPost(@Valid MenuDTO menuDTO, BindingResult bindingResult,
                                 List<MultipartFile> multipartFile, Model model) {

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 에러");
            log.info(bindingResult.getAllErrors()); //확인된 모든 에러 콘솔창 출력

            return "menu/boupdatemenu";  //다시 이전페이지로
        }

        try {
            // 새로운 이미지들 저장 처리
            String join = "menu";

            // 업데이트 서비스
            menuService.menuUpdate(menuDTO, join, multipartFile);

        } catch (Exception e) {
            log.error("메뉴 업데이트 오류: ", e);
            // 오류 발생 시 모델에 에러 메시지를 추가하거나 다시 페이지로 이동
            model.addAttribute("msg", "메뉴 업데이트 중 오류가 발생했습니다.");
            return "menu/boupdatemenu";
        }

        return "redirect:/admin/menu/bo/adlistmenu";

    }

    @GetMapping("/menu/removemenu")
    @ResponseBody
    public ResponseEntity<String> removeMenu(@RequestParam Integer idx) {

        // 이미지 조회전 join값 생성
        String join = "menu";

        try {
            // idx로 데이터를 조회하여 삭제
            menuService.menuRemove(idx, join);

            return ResponseEntity.ok("삭제하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제를 실패하였습니다.");
        }
    }

}