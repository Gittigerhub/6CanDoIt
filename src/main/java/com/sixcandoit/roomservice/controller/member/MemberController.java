package com.sixcandoit.roomservice.controller.member;

import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.service.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Log4j2
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/")
    public String IndexForm(HttpSession session, MemberDTO memberDTO) {
        session.setAttribute("memberName", memberDTO.getMemberName());
        return "memberindex";
    }

    // 로그인
    @GetMapping("/login")
    public String showLoginPage(){
        log.info("로그인 페이지!");

        return "member/login";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String showLogoutPage(HttpSession session){

        session.invalidate();

        return "redirect:/member/login";
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
       log.info("진입여부");
        boolean exists = memberService.checkEmailExistence(email);
        String result = exists? "1" :   "0";
        log.info("email exists : " + email+exists);

        return result;  // 응답 반환
    }

    // 연락처 중복 확인
    @PostMapping("/checkPhone")
    @ResponseBody
    public String checkPhone(@RequestParam("phone") String phone) {
        log.info("진입여부 2");
        boolean exists = memberService.checkPhoneExistence(phone); // 연락처 중복 여부 확인
        String result = exists ? "1" : "0"; // 중복되면 "1", 아니면 "0" 반환
        log.info("phone exists : " + phone + exists);

        return result;  // 응답 반환
    }

    // 회원 수정 전 비밀번호 확인 페이지
    @GetMapping("/verify")
    public String ShowPasswordVerificationPage(HttpSession session, Model model){
        String memberEmail = (String) session.getAttribute("memberEmail");
        model.addAttribute("memberEmail",memberEmail );
        return "/member/verify";
    }

    // 비밀번호 검증 처리
    @PostMapping("/verify")
    public String verifyPassword(@RequestParam String password,
                                 @RequestParam String memberEmail,
                                 HttpSession session){

        // 비밀번호를 DB에서 조회하여 비교
        boolean isPasswordValid = memberService.verifyPassword(password, memberEmail);

        if (isPasswordValid) {
            // 비밀번호가 맞으면 회원 정보 수정 페이지로 리디렉션
            session.setAttribute("email", memberEmail);  // 이메일을 세션에 저장
            System.out.println("비밀번호가 맞습니다.");
            return "redirect:/member/modify";  // 수정 페이지로 이동
        } else {
            // 비밀번호가 틀리면 다시 비밀번호 확인 페이지로 돌아가기
            System.out.println("비밀번호가 틀렸습니다.");
            return "redirect:/member/verify";  // 비밀번호 페이지로 리디렉션
        }
    }

    // 회원 정보 수정
    @GetMapping("/modify")
    public String showModifyPage(HttpSession session, Model model){
        String memberEmail = (String) session.getAttribute("memberEmail");
        MemberDTO memberDTO = new MemberDTO();

        if (memberEmail != null){
            log.info("멤버 이메일 ? " + memberEmail);
            memberDTO = memberService.read(memberEmail);
        }

        model.addAttribute("memberDTO", memberDTO);
        return "member/modify";
    }

    @PostMapping("/modify")
    public String modifyMember(@ModelAttribute MemberDTO memberDTO){
        memberService.modify(memberDTO);
        log.info("회원 정보를 수정해요!!");

        return "redirect:/member/";
    }

    // 비밀번호 수정
    @GetMapping("/modifypw")
    public String showModifyPWPage(HttpSession session, Model model){
        log.info("비밀번호 변경을 해요!!");
        return "member/modifypw";
    }

    @PostMapping("/modifypw")
    public String modifyPW(@RequestParam String currentPassword,
                           @RequestParam String newPassword,
                           HttpSession session, RedirectAttributes redirectAttributes){
        String memberEmail = (String) session.getAttribute("memberEmail");

        if (memberEmail == null) {
            return "redirect:/login"; // 로그인 안 했으면 로그인 페이지로 이동
        }

        boolean isUpdated = memberService.changePassword(memberEmail, currentPassword, newPassword);

        if (!isUpdated) {
            redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/member/modifypw";
        }

        redirectAttributes.addFlashAttribute("success", "비밀번호가 변경되었습니다.");
        return "redirect:/member/";
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

    // 회원 삭제 (일반 회원)
    @PostMapping("/deleteMember")
    @ResponseBody
    public String deleteMember(@RequestParam Integer idx) {
        log.info("회원 삭제 요청: " + idx);

        boolean isDeleted = memberService.deleteMember(idx);
        return isDeleted ? "success" : "fail";
    }



}
