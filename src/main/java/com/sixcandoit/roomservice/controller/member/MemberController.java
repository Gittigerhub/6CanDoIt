package com.sixcandoit.roomservice.controller.member;

import com.sixcandoit.roomservice.config.CustomUserDetails;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersDTO;
import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.service.EmailService;
import com.sixcandoit.roomservice.service.member.MemberService;
import com.sixcandoit.roomservice.service.room.ReservationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Log4j2
public class MemberController {

    private final MemberService memberService;
    private final ReservationService reservationService;
    private final EmailService email;
    private final EmailService emailService;

    @GetMapping("/")
    public String IndexForm(HttpSession session, MemberDTO memberDTO) {
        session.setAttribute("memberName", memberDTO.getMemberName());
        return "memberindex";
    }

    // 로그인
    @GetMapping("/login")
    public String showLoginPage(){
        log.info("로그인 페이지!");
        return "member/sign";
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
        return "member/sign";
    }

    @PostMapping("/register")
    public String registerProc(@ModelAttribute MemberDTO memberDTO){
        if (memberService.register(memberDTO) == null){
            return "redirect:/member/login";
        }
        return "redirect:/member/login";
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
        log.info("세션에서 가져온 memberEmail: " + memberEmail);
        
        if (memberEmail == null) {
            log.error("세션에 memberEmail이 없습니다.");
            return "redirect:/member/login";
        }
        
        model.addAttribute("memberEmail", memberEmail);
        return "/member/verify";
    }

    // 비밀번호 검증 처리
    @PostMapping("/verify")
    public String verifyPassword(@RequestParam String password,
                                 @RequestParam String memberEmail,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes){

        log.info("비밀번호 검증 시도 - 이메일: " + memberEmail);
        
        // 비밀번호를 DB에서 조회하여 비교
        boolean isPasswordValid = memberService.verifyPassword(password, memberEmail);
        log.info("비밀번호 검증 결과: " + isPasswordValid);

        if (isPasswordValid) {
            // 비밀번호가 맞으면 회원 정보 수정 페이지로 리디렉션
            session.setAttribute("memberEmail", memberEmail);
            log.info("비밀번호 검증 성공 - 세션에 memberEmail 저장: " + memberEmail);
            return "redirect:/member/modify";
        } else {
            // 비밀번호가 틀리면 에러 메시지와 함께 다시 비밀번호 확인 페이지로 돌아가기
            log.error("비밀번호 검증 실패 - 이메일: " + memberEmail);
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/verify";
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
    @PostMapping("/password")
    @ResponseBody
    public String modifyPassword(@RequestBody MemberDTO memberDTO){
        log.info("패스워드 컨트롤러 진입 여부");
        boolean result = memberService.passwordSend(memberDTO);
        return result ? "success" : "fail";
    }

    // 회원 삭제 (일반 회원)
    @PostMapping("/deleteMember")
    @ResponseBody
    public String deleteMember(@RequestParam Integer idx) {
        log.info("회원 삭제 요청: " + idx);

        boolean isDeleted = memberService.deleteMember(idx);
        return isDeleted ? "success" : "fail";
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @PageableDefault(page=1) Pageable page, Model model) {

        // 회원정보 조회
        MemberDTO memberDTO = memberService.read(userDetails.getUsername());

        // 사용자의 예약 목록 조회
        List<ReservationDTO> reservations = reservationService.getUserReservations(userDetails.getUsername());

        // 서비스에 주문 내역 조회 요청
        Page<OrdersDTO> ordersDTOS = memberService.memberOrderList(memberDTO.getMemberEmail(), page);

        // 뷰로 데이터 전달
        model.addAttribute("memberDTO", memberDTO);
        model.addAttribute("reservations", reservations);
        model.addAttribute("ordersDTOS", ordersDTOS);

        return "member/mypage";
    }

    @PostMapping("/sendEmailCode")
    @ResponseBody
    public ResponseEntity<String> sendEmail(@RequestParam("email") String email){
        log.info("이메일 발송 컨트롤러 진입");

        try {
            emailService.codeEmailSending(email, 2);
            //admin과 member를 구분하기 위해(admin = 1, member = 2)
            return ResponseEntity.status(200).body(email);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("이메일 발송 에러");
        }
    }
    @PostMapping("/checkEmailCode")
    @ResponseBody
    public ResponseEntity<String> CheckEmailCode(@RequestParam("email") String email, @RequestParam("authenticationCode") String authenticationCode){
        try {
            String result = emailService.checkEmailCode(email, authenticationCode)+"";
            return ResponseEntity.ok().body(result);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("에러 발생");
        }
    }


}
