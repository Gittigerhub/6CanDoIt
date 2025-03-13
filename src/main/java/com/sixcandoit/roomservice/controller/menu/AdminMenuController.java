package com.sixcandoit.roomservice.controller.menu;

import com.sixcandoit.roomservice.config.CustomUserDetails;
import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.Menu.MenuDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.dto.office.ShopDetailDTO;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.menu.MenuService;
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
import org.springframework.security.core.context.SecurityContextHolder;
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

    //아이템 등록
    @GetMapping("/menu/registermenu")
    public String registerMenu(Model model, Principal principal) {
        if (principal == null) {
            //로그인이 안되어 있으면, 접근 불가능하도록
            return "redirect:/login";
        }
        System.out.println("이메일 : " + principal.getName());
        try {
            // SecurityContextHolder에서 현재 인증된 사용자 정보 가져오기
            Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            System.out.println("지나감 1");

            if (userDetails instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                AdminEntity adminEntity = customUserDetails.getAdmin();

                System.out.println("지나감 2");

                // 관리자의 조직 정보 가져오기
                OrganizationEntity organizationEntity = adminEntity.getOrganizationJoin();

                System.out.println("지나감 3");

                // DTO로 변환
                OrganizationDTO organizationDTO = modelMapper.map(organizationEntity, OrganizationDTO.class);

                System.out.println("지나감 4");

                if (organizationDTO != null) {
                    // 조직 정보로 매장 정보 조회
                    ShopDetailDTO shopDetailDTO = shopDetailService.findOrgan(9);

                    System.out.println("지나감 5");

                    if (shopDetailDTO != null) {
                        // 모델에 필요한 정보 추가
                        model.addAttribute("shopDetailDTO", shopDetailDTO);
                    }

                    System.out.println("지나감 6");

                    // 모델에 필요한 정보 추가
                    model.addAttribute("organizationDTO", organizationDTO);

                } else {
                    System.out.println("organizationDTO가 NULL이야 ");
                }

            }
        } catch (Exception e) {
            log.error("관리자 정보 조회 중 오류 발생: ", e);

        }
        model.addAttribute("menuDTO", new MenuDTO());
        return "/menu/registermenu";
    }

    //아이템 등록
    @PostMapping("/menu/registermenu")
    public String registerMenuPost(@Valid MenuDTO menuDTO, BindingResult bindingResult,
                                   List<MultipartFile> multipartFile,
                                   @RequestParam("organ_idx") Integer organIdx,
                                   @RequestParam("shop_detail_idx") Integer shopDetailIdx,
                                   Model model) {
        //들어오는 값을 확인
        log.info("들어오는 값 확인 " + menuDTO);
        log.info("조직 ID: " + organIdx);
        log.info("매장 ID: " + shopDetailIdx);

        if (multipartFile.get(0).isEmpty()){
            model.addAttribute("msg", "대표이미지 등록은 필수입니다.");
            return "/menu/registermenu";
        }

        // 파일 형식 검증 : 모든 파일이 이미지 파일인지에 대해 확인
        for (MultipartFile img : multipartFile) {
            if(!img.isEmpty()) {
                //파일 정보 확인 로그 추가
                log.info("파일 이름: " + img.getOriginalFilename());
                log.info("파일 크기: " + img.getSize() + " bytes");
                log.info("파일 타입: " + img.getContentType());

                //파일 형식 검증
                String contentType = img.getContentType();  //MIME 타입 확인
                log.info("업로드 된 파일 MIME 타입 : " + contentType);

                if (contentType == null || !contentType.startsWith("image")) {
                    model.addAttribute("msg", "이미지 파일만 업로드 가능합니다.");
                            return "/menu/registermenu";  //이미지 파일이 아니라면 다시 처음으로 돌아감
                }
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 에러");
            log.info(bindingResult.getAllErrors());     //확인된 모든 에러 콘솔창 출력

            return "/menu/registermenu";  //다시 이전 페이지
        }
        try {

            menuService.menuRegister(menuDTO, multipartFile, organIdx, shopDetailIdx);

            log.info("상품 등록 완료!");

            return "redirect:/admin/menu/adlistmenu";

        }catch (Exception e) {
            e.printStackTrace();
            log.info("파일 등록시 문제가 발생했습니다.");
            model.addAttribute("msg", "올바른 파일을 등록하세요.");
            return "/menu/registermenu";  //다시 이전 페이지로
        }
    }

    //메뉴 목록 확인
    @GetMapping("/menu/adreadmenu")
    public String readMenu(Integer menuidx, Model model, RedirectAttributes redirectAttributes) {

        // menuidx가 null인 경우 처리
        if (menuidx == null) {
            redirectAttributes.addFlashAttribute("msg", "메뉴 ID가 없습니다.");
            return "redirect:/admin/menu/adlistmenu";
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
            model.addAttribute("bucket", bucket);
            model.addAttribute("region", region);
            model.addAttribute("folder", folder);
            model.addAttribute("imageFileDTOList", imageFileDTOList);
            model.addAttribute("hasRepImage", hasRepImage);

            return "/menu/adreadmenu";
        }catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 메뉴입니다.");
            return "redirect:/admin/menu/adreadmenu";
        } catch (Exception e) {
            // 예기치 않은 예외 처리 (디버깅용 로그 등 추가 가능)
            redirectAttributes.addFlashAttribute("msg", "알 수 없는 오류가 발생했습니다.");
            return "redirect:/admin/menu/adreadmenu";
        }
    }

    //메뉴 목록 확인　｀
    @GetMapping("/menu/adlistmenu")
    public String listMenu(@PageableDefault(page = 1) Pageable page, //페이지 정보
                           @RequestParam(value = "type", defaultValue = "") String type, //검색 대상
                           @RequestParam(value = "keyword", defaultValue = "") String keyword, //키워드
                           @RequestParam(value = "organIdx") Integer organIdx, //매장 상세
                           Model model){

        String join = "menu";

        //해당페이지의 내용을 서비스를 통해 데이터베이스로부터 조회
        Page<MenuDTO> menuDTOS = menuService.menuList(page, type, keyword);

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
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("organIdx", organIdx);  // organIdx 추가

        return "menu/adlistmenu";

    }

    //Ajax 요청에 의해 카테고리별 메뉴목록을 반환하는 메소드
    @GetMapping("/menu/ajax/adlistmenu")
    public String ajaxListMenu(@RequestParam(value = "category", defaultValue = "ALL") String category,
                               @PageableDefault(page = 1) Pageable page,
                               Model model){

        // 해당 페이지의 내용을 서비스를 통해 데이터베이스로부터 조회
        Page<MenuDTO> menuDTOS = menuService.selectCate(page, category);

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

    @GetMapping("/menu/updatemenu")
    public String updateMenu(@RequestParam("idx") Integer menuidx, Model model, Principal principal ) {

        String join = "menu";

        // menuidx가 null인 경우 처리
        if (menuidx == null) {
            return "redirect:/admin/menu/adlistmenu";
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
            return "/menu/updatemenu";//수정페이지로 이동
        }else {
            return "redirect:/admin/menu/adlistmenu";
        }
    }

    @PostMapping("/menu/updatemenu")
    public String updateMenuPost(@Valid MenuDTO menuDTO, BindingResult bindingResult,
                                 List<MultipartFile> multipartFile, Model model) {

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 에러");
            log.info(bindingResult.getAllErrors()); //확인된 모든 에러 콘솔창 출력

            return "/menu/updatemenu";  //다시 이전페이지로
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
            return "/menu/updatemenu";
        }

        return "redirect:/admin/menu/adlistmenu";

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