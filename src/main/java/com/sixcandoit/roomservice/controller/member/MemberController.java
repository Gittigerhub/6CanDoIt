package com.sixcandoit.roomservice.controller.member;

import com.sixcandoit.roomservice.dto.admin.AdminDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.service.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Log4j2
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/")
    public String IndexForm(HttpSession session, MemberDTO memberDTO) {
        session.setAttribute("memberName", memberDTO.getMemberName());
        return "index";
    }

    // 회원가입
    @GetMapping("/register")
    public String memberRegister() {
        return "member/register";
    }


    @PostMapping("/register")
    public String registerProc(@ModelAttribute MemberDTO memberDTO){

        if (memberService.register(memberDTO) == null){
            return "redirect:/register";
        }

        return "redirect:/";
    }

    // 이메일 중복 확인
    @PostMapping("/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email) {
       log.info("진입여부 헤이명선");
        boolean exists = memberService.checkEmailExistence(email);
        String result = exists? "1" :   "0";
        log.info("email exists : " + email+exists);

        return result;  // 응답 반환
    }

    // 연락처 중복 확인
    @PostMapping("/checkPhone")
    @ResponseBody
    public String checkPhone(@RequestParam("phone") String phone) {
        log.info("진입여부 헤이명선2");
        boolean exists = memberService.checkPhoneExistence(phone); // 연락처 중복 여부 확인
        String result = exists ? "1" : "0"; // 중복되면 "1", 아니면 "0" 반환
        log.info("phone exists : " + phone + exists);

        return result;  // 응답 반환
    }



    // 회원 수정
    @GetMapping("/modify")
    public String showModifyPage(HttpSession session, Model model){
        String memberEmail = (String) session.getAttribute("memberEmail");
        MemberDTO memberDTO = new MemberDTO();

        if (memberEmail != null){
            memberDTO = memberService.read(memberEmail);
        }

        model.addAttribute("memberDTO", memberDTO);
        return "member/modify";
    }

    @PostMapping("/modify")
    public String modifyMember(@ModelAttribute MemberDTO memberDTO){
        memberService.modify(memberDTO);

        return "redirect:/logout";
    }

    // 임시비밀번호 발급
    @GetMapping("/password")
    public String showPasswordPage(){
        return "member/password";
    }

    @PostMapping("/password")
    public String modifyPassword(@ModelAttribute MemberDTO memberDTO){
        log.info("패스워드 컨트롤러 진입 여부");
        memberService.passwordSend(memberDTO);
        //스크립트로 출력할 메세지를 전달

        return "redirect:/member/login";
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

        session.invalidate();

        return "redirect:/login";
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
