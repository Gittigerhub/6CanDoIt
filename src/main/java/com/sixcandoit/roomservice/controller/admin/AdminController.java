package com.sixcandoit.roomservice.controller.admin;

import com.sixcandoit.roomservice.dto.admin.AdminDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.service.admin.AdminService;
import com.sixcandoit.roomservice.service.member.MemberService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Log4j2
public class AdminController {

    private final AdminService adminService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @GetMapping("/")
    public String IndexForm(HttpSession session, AdminDTO adminDTO) {
        session.setAttribute("adminName", adminDTO.getAdminName());
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "admin/register";
    }

    @PostMapping("/register")
    public String registerProc(@ModelAttribute AdminDTO adminDTO){

        if (adminService.register(adminDTO) == null){
            return "redirect:admin/register";
        }

        return "redirect:/";
    }

    // 이메일 중복 확인
    @PostMapping("/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email) {
        log.info("진입여부");
        boolean exists = adminService.checkEmailExistence(email);
        String result = exists? "1" :   "0";
        log.info("email exists : " + email+exists);

        return result;  // 응답 반환
    }

    // 회원 수정
    @GetMapping("/modify")
    public String showModifyPage(HttpSession session, Model model){
        String adminEmail = (String) session.getAttribute("adminEmail");
        AdminDTO adminDTO = new AdminDTO();

        if (adminEmail != null){
            adminDTO = adminService.read(adminEmail);
        }

        model.addAttribute("adminDTO", adminDTO);
        return "admin/modify";
    }

    @PostMapping("/modify")
    public String modifyMember(@ModelAttribute AdminDTO adminDTO){
        adminService.modify(adminDTO);

        return "redirect:/logout";
    }

    // 임시비밀번호 발급
    @GetMapping("/password")
    public String showPasswordPage(){
        return "admin/password";
    }

    @PostMapping("/password")
    public String modifyPassword(@ModelAttribute AdminDTO adminDTO){
        adminService.passwordSend(adminDTO);
        //스크립트로 출력할 메세지를 전달

        return "redirect:/login";
    }

    // 로그인
    @GetMapping("/login")
    public String showLoginPage(){
        log.info("showLoginPage");

        return "admin/login";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String showLogoutPage(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

    // 일반 회원 목록
    @GetMapping("/memberlist")
    public String showMemberList(@PageableDefault(page = 1) Pageable page, // 페이지 정보
                                 @RequestParam(value = "type", defaultValue = "") String type, // 검색대상
                                 @RequestParam(value = "keyword", defaultValue = "") String keyword, // 키워드
                                 Model model){
        // 해당 페이지의 내용을 서비스를 통해 데이터베이스로 부터 조회
        Page<MemberDTO> memberDTOS = memberService.memberList(page, type, keyword);

        // html에 필요한 페이지 정보를 받는다
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(memberDTOS); // 내가 알기론 이거 내부 값들을 개별적으로 애드어트리뷰트해서 넘겨줘야하는걸로 기억하는데

        model.addAttribute("member", memberDTOS); // 데이터 전달
        model.addAttribute(pageInfo); // 페이지 정보
        model.addAttribute("type", type); // 검색 분류
        model.addAttribute("keyword", keyword); // 키워드


//        List<MemberEntity> memberList = memberRepository.findAll();
//        model.addAttribute("memberList", memberList);
        return "admin/memberlist";
    }
}
