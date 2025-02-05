package com.sixcandoit.roomservice.controller.member;

import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.dto.member.MemberLoginDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.service.admin.AdminLoginService;
import com.sixcandoit.roomservice.service.member.MemberLoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Log4j2
public class MemberLoginController {
    private final MemberLoginService memberLoginService; // 서비스 연동
    private final AdminLoginService adminLoginService;

    @GetMapping("/")
    public String IndexForm(HttpSession session, MemberDTO memberDTO) {
        session.setAttribute("memberName", memberDTO.getMemberName());
        return "index";
    }

    // 회원가입
    @GetMapping("/register")
    public String showRegisterPage(){
        return "member/register";
    }

    @PostMapping("/register")
    public String registerMember(@ModelAttribute MemberLoginDTO memberLoginDTO){
        memberLoginService.saveMember(memberLoginDTO);

        return "redirect:/member/login";
    }

    // 회원 수정
    @GetMapping("/modify/{idx}")
    public String showModifyPage(@PathVariable Integer idx, Model model){
        MemberEntity member = memberLoginService.findByIdx(idx);
        model.addAttribute("member", member);
        return "member/modify";
    }

    @PostMapping("/modify")
    public String modifyMember(@ModelAttribute MemberLoginDTO memberLoginDTO){

        return "redirect:/logout";
    }

    // 임시비밀번호 발급
    @GetMapping("/password")
    public String showPasswordPage(){
        return "member/password";
    }

    @PostMapping("/password")
    public String modifyPassword(@ModelAttribute MemberLoginDTO memberLoginDTO){
        memberLoginService.passwordSend(memberLoginDTO);
        //스크립트로 출력할 메세지를 전달

        return "redirect:/login";
    }

    // 로그인
    @GetMapping("/login")
    public String showLoginPage(){
        log.info("showLoginPage");

        return "member/login";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String showLogoutPage(HttpSession session){
        session.invalidate(); // 해당 접속 컴퓨터의 섹션 정보를 삭제

        return "redirect:/"; // 메인 또는 로그인
    }


//    @GetMapping("/member")
//    public String MemberForm() {  //Member 권한자만 접근 가능
//        return "member";
//    }
//
//    @GetMapping("/admin")
//    public String AdminForm() { //ADMIN 권한자만 접근 가능
//        return "admin";
//    }
//
//    @GetMapping("/ho")
//    public String HoForm() {
//        return "ho";
//    }
//
//    @GetMapping("/bo")
//    public String BoForm() {
//        return "bo";
//    }
//
//    @GetMapping("/manager")
//    public String ManagerForm() {
//        return "manager";
//    }

}
