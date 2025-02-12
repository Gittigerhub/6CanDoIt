package com.sixcandoit.roomservice.controller.orders;

import com.sixcandoit.roomservice.dto.orders.MenuDTO;
import com.sixcandoit.roomservice.service.orders.MenuService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MenuController {

    private final MenuService menuService;

    //아이템 등록
    @GetMapping("/orders/registermenu")
    public String registerMenu(Model model, Principal principal) {
        if (principal == null) {
            //로그인이 안되어 있으면, 접근 불가능하도록
            return "redirect:/login";
        }
        if (principal != null) {
            log.info("현재 로그인 한 사람");
            log.info(principal.getName());
        }
        model.addAttribute("menuDTO", new MenuDTO());
        return "/orders/registermenu";
    }

    //아이템 등록
    @PostMapping("/orders/registermenu")
    public String registerMenuPost(@Valid MenuDTO menuDTO, BindingResult bindingResult,
                                   List<MultipartFile> multipartFile, Model model) {
        //들어오는 값을 확인
        log.info("들어오는 값 확인 " + menuDTO);

        if (multipartFile.get(0).isEmpty()){
            model.addAttribute("msg", "대표이미지 등록은 필수입니다.");
            return "/orders/registermenu";
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
                            return "/orders/registermenu";  //이미지 파일이 아니라면 다시 처음으로 돌아감
                }
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 에러");
            log.info(bindingResult.getAllErrors());     //확인된 모든 에러 콘솔창 출력

            return "/orders/registermenu";  //다시 이전 페이지
        }
        try {

            Integer savedMenuidx
                    = menuService.menuRegister(menuDTO, multipartFile);

            log.info("상품 등록 완료!");

            return "redirect:/orders/registermenu?idx=" + savedMenuidx;
        }catch (Exception e) {
            e.printStackTrace();
            log.info("파일 등록시 문제가 발생했습니다.");
            model.addAttribute("msg", "올바른 파일을 등록하세요.");
            return "/orders/registermenu";  //다시 이전 페이지로
        }
    }

    //메뉴 목록 확인
    @GetMapping("/orders/readmenu")
    public String readMenu(Integer menuidx, Model model, RedirectAttributes redirectAttributes) {

        try {
            MenuDTO menuDTO = menuService.menuRead(menuidx);
            log.info("menuDTO: " + menuDTO);
            model.addAttribute("menuDTO", menuDTO);

            return "/orders/readmenu";
        }catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 메뉴입니다.");
            return "redirect:/orders/readmenu";
        }
    }

    @GetMapping("/orders/menulist")
    public String listMenu(@PageableDefault(page = 1)Pageable pagea, //페이지 정보
                           @RequestParam(value = "type", defaultValue = "") String type, //검색 대상
                           @RequestParam(value = "keyword", defaultValue = "") String keyword, //키워드
                           Model model){
        //해당페이지의 내용을 서비스를 통해 데이터베이스로부터 조회
        Page<MenuDTO> menuDTOS = menuService.menuList(pagea, type, keyword);
        //html에 필요한 페이지 정보를 받기
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(menuDTOS);

        model.addAttribute("menulist", menuDTOS); //데이터 전달
        model.addAttribute(pageInfo);   //페이지 정보
        model.addAttribute("type", type);   //검색 분류
        model.addAttribute("keyword", keyword); //키워드

        return "orders/menulist";

    }

}
