package com.sixcandoit.roomservice.controller.member;

import com.sixcandoit.roomservice.config.CustomUserDetails;
import com.sixcandoit.roomservice.dto.AdvertisementDTO;
import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersDTO;
import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.service.AdvertisementService;
import com.sixcandoit.roomservice.service.EmailService;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.member.MemberService;
import com.sixcandoit.roomservice.service.qna.QnaService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Log4j2
public class MemberController {

    // 의존성 주입
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final QnaService qnaService;
    private final EmailService email;
    private final EmailService emailService;
    private final AdvertisementService advertisementService;
    private final ImageFileService imageFileService;


    /* -----------------------------------------------------------------------------
        경로 : /member/ (GET)
        인수 : HttpSession session, MemberDTO memberDTO, Model model
        출력 : "memberindex" 페이지
        설명 : 회원 메인 페이지로 이동하며, 광고 목록과 해당 광고에 대한 이미지 정보를 세션과 모델에 저장
    ----------------------------------------------------------------------------- */
    @GetMapping("/")
    public String IndexForm(HttpSession session, MemberDTO memberDTO, Model model) {

        // 조회할 광고 가져오기
        List<AdvertisementDTO> advertisementDTOS = advertisementService.adMemberList();

        // 이미지 조회전 join값 생성
        String join = "adver";

        // 이미지 데이터를 담을 Map 생성
        Map<Integer, List<ImageFileDTO>> imageFileMap = new HashMap<>();
        Map<Integer, Boolean> repImageMap = new HashMap<>();

        for (AdvertisementDTO advertisementDTO : advertisementDTOS) {
            // 이미지 조회
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(advertisementDTO.getIdx(), join);

            // Map에 저장 (advertisementDTO의 idx를 key로 저장)
            imageFileMap.put(advertisementDTO.getIdx(), imageFileDTOS);

            // 대표 사진 여부 확인 후 저장
            boolean hasRepImage = imageFileDTOS.stream()
                    .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

            repImageMap.put(advertisementDTO.getIdx(), hasRepImage);
        }

        session.setAttribute("memberName", memberDTO.getMemberName());
        model.addAttribute("advertisementDTOS", advertisementDTOS);  // 데이터 전달
        model.addAttribute("imageFileMap", imageFileMap);            // 메뉴별 이미지 리스트
        model.addAttribute("repImageMap", repImageMap);              // 메뉴별 대표 사진 여부

        return "memberindex";
    }

    // 로그인
    /* -----------------------------------------------------------------------------
        경로 : /member/login (GET)
        인수 : 없음
        출력 : "member/sign" 페이지
        설명 : 로그인 페이지로 이동
    ----------------------------------------------------------------------------- */
    @GetMapping("/login")
    public String showLoginPage(){
        log.info("로그인 페이지!");
        return "member/sign";
    }

    // 로그아웃
     /* -----------------------------------------------------------------------------
        경로 : /member/logout (GET)
        인수 : HttpSession session
        출력 : 로그인 페이지로 리디렉션
        설명 : 로그아웃 처리 후 로그인 페이지로 리디렉션
    ----------------------------------------------------------------------------- */
    @GetMapping("/logout")
    public String showLogoutPage(HttpSession session){
        session.invalidate();
        return "redirect:/member/login";
    }

    // 회원가입
    /* -----------------------------------------------------------------------------
        경로 : /member/register (GET)
        인수 : 없음
        출력 : "member/sign" 페이지
        설명 : 회원가입 페이지로 이동
    ----------------------------------------------------------------------------- */
    @GetMapping("/register")
    public String memberRegister() {
        return "member/sign";
    }

    /* -----------------------------------------------------------------------------
        경로 : /member/register (POST)
        인수 : @ModelAttribute MemberDTO memberDTO
        출력 : 로그인 페이지로 리디렉션
        설명 : 회원가입 처리 후 로그인 페이지로 리디렉션
    ----------------------------------------------------------------------------- */
    @PostMapping("/register")
    public String registerProc(@ModelAttribute MemberDTO memberDTO){
        if (memberService.register(memberDTO) == null){
            return "redirect:/member/login";
        }
        return "redirect:/member/login";
    }

    // 이메일 중복 확인
    /* -----------------------------------------------------------------------------
        경로 : /member/checkEmail (POST)
        인수 : @RequestParam("email") String email
        출력 : "1" 또는 "0"
        설명 : 이메일 중복 여부를 확인하고 중복되면 "1", 없으면 "0" 반환
    ----------------------------------------------------------------------------- */
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
    /* -----------------------------------------------------------------------------
        경로 : /member/checkPhone (POST)
        인수 : @RequestParam("phone") String phone
        출력 : "1" 또는 "0"
        설명 : 연락처 중복 여부를 확인하고 중복되면 "1", 없으면 "0" 반환
    ----------------------------------------------------------------------------- */
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
    /* -----------------------------------------------------------------------------
    경로 : /member/verify (GET)
    인수 : HttpSession session, Model model
    출력 : "member/verify" 페이지
    설명 : 회원 수정 전에 비밀번호를 확인하는 페이지로 이동. 세션에서 memberEmail을 가져와서 해당 이메일에 대한 비밀번호를 확인하는 절차를 시작.
    만약 세션에 memberEmail이 없으면 로그인 페이지로 리디렉션.
 ----------------------------------------------------------------------------- */
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
    /* -----------------------------------------------------------------------------
    경로 : /member/verify (POST)
    인수 : String password, String memberEmail, HttpSession session, RedirectAttributes redirectAttributes
    출력 : 회원 정보 수정 페이지로 리디렉션 또는 비밀번호 확인 페이지로 리디렉션
    설명 : 사용자가 입력한 비밀번호를 DB에 저장된 비밀번호와 비교하여 검증.
    비밀번호가 일치하면 회원 정보 수정 페이지로 리디렉션하고, 그렇지 않으면 비밀번호 확인 페이지로 다시 리디렉션.
 ----------------------------------------------------------------------------- */
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
    /* -----------------------------------------------------------------------------
    경로 : /member/modify (GET)
    인수 : HttpSession session, Model model
    출력 : "member/modify" 페이지
    설명 : 회원 정보 수정 페이지로 이동. 세션에서 회원 이메일을 가져와 해당 이메일로 회원 정보를 조회 후,
    수정할 회원 정보를 모델에 전달하여 수정 페이지를 띄운다.
 ----------------------------------------------------------------------------- */
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

    /* -----------------------------------------------------------------------------
    경로 : /member/modify (POST)
    인수 : @ModelAttribute MemberDTO memberDTO
    출력 : 회원 목록 페이지로 리디렉션
    설명 : 회원 수정 정보를 처리 후, 수정된 회원 정보를 DB에 저장하고, 회원 목록 페이지로 리디렉션.
 ----------------------------------------------------------------------------- */
    @PostMapping("/modify")
    public String modifyMember(@ModelAttribute MemberDTO memberDTO){
        memberService.modify(memberDTO);
        log.info("회원 정보를 수정해요!!");

        return "redirect:/member/";
    }

    // 비밀번호 수정
    /* -----------------------------------------------------------------------------
    경로 : /member/modifypw (GET)
    인수 : 없음
    출력 : "member/modifypw" 페이지
    설명 : 비밀번호 수정 페이지로 이동. 현재 비밀번호와 새 비밀번호를 입력받을 수 있는 페이지 제공.
 ----------------------------------------------------------------------------- */
    @GetMapping("/modifypw")
    public String showModifyPWPage(HttpSession session, Model model){
        log.info("비밀번호 변경을 해요!!");
        return "member/modifypw";
    }

    /* -----------------------------------------------------------------------------
    경로 : /member/modifypw (POST)
    인수 : String currentPassword, String newPassword, HttpSession session, RedirectAttributes redirectAttributes
    출력 : 회원 목록 페이지로 리디렉션 또는 비밀번호 수정 페이지로 리디렉션
    설명 : 사용자가 입력한 현재 비밀번호와 새 비밀번호를 비교하여 비밀번호를 변경.
    비밀번호 변경에 성공하면 회원 목록 페이지로 리디렉션하고, 실패하면 비밀번호 수정 페이지로 돌아가 에러 메시지 표시.
 ----------------------------------------------------------------------------- */
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
    /* -----------------------------------------------------------------------------
    경로 : /member/password (POST)
    인수 : MemberDTO memberDTO
    출력 : "success" 또는 "fail"
    설명 : 사용자가 요청한 이메일로 임시 비밀번호를 발급하여 해당 이메일로 전송.
    memberService의 passwordSend 메서드를 호출하여 임시 비밀번호를 발급하고, 성공 여부에 따라 결과를 반환.
 ----------------------------------------------------------------------------- */
    @PostMapping("/password")
    @ResponseBody
    public String modifyPassword(@RequestBody MemberDTO memberDTO){
        log.info("패스워드 컨트롤러 진입 여부");
        boolean result = memberService.passwordSend(memberDTO);
        return result ? "success" : "fail";
    }

    // 회원 삭제 (일반 회원)
    /* -----------------------------------------------------------------------------
    경로 : /member/deleteMember (POST)
    인수 : Integer idx
    출력 : "success" 또는 "fail"
    설명 : 회원 삭제 요청을 처리. idx를 이용해 삭제할 회원을 특정하고, memberService의 deleteMember 메서드를 호출하여 회원을 삭제.
 ----------------------------------------------------------------------------- */
    @PostMapping("/deleteMember")
    @ResponseBody
    public String deleteMember(@RequestParam Integer idx) {
        log.info("회원 삭제 요청: " + idx);

        boolean isDeleted = memberService.deleteMember(idx);
        return isDeleted ? "success" : "fail";
    }

    // 마이페이지
    /* -----------------------------------------------------------------------------
    경로 : /member/mypage (GET)
    인수 : @AuthenticationPrincipal CustomUserDetails userDetails, @PageableDefault(page=1) Pageable page, Model model
    출력 : "member/mypage" 페이지
    설명 : 로그인한 사용자의 마이페이지를 조회. 사용자의 정보, 예약 목록, 주문 내역, 최근 문의 내역을 모델에 담아 뷰로 전달.
 ----------------------------------------------------------------------------- */
    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @PageableDefault(page=1) Pageable page, Model model) {

        // 회원정보 조회
        MemberDTO memberDTO = memberService.read(userDetails.getUsername());

        // 사용자의 예약 목록 조회
        List<ReservationDTO> reservations = reservationService.getUserReservations(userDetails.getUsername());

        // 서비스에 주문 내역 조회 요청
        Page<OrdersDTO> ordersDTOS = memberService.memberOrderList(memberDTO.getMemberEmail(), page);

        // 최근 문의 내역 3개 조회
        List<QnaDTO> recentQnas = qnaService.getRecentQnasByMember(memberDTO.getIdx());

        // 뷰로 데이터 전달
        model.addAttribute("memberDTO", memberDTO);
        model.addAttribute("reservations", reservations);
        model.addAttribute("ordersDTOS", ordersDTOS);
        model.addAttribute("recentQnas", recentQnas);

        return "member/mypage";
    }

    // 이메일 인증 코드 전송
    /* -----------------------------------------------------------------------------
    경로 : /member/sendEmailCode (POST)
    인수 : String email
    출력 : ResponseEntity<String> (상태 200 또는 400)
    설명 : 사용자가 입력한 이메일로 인증 코드를 발송. 이메일 발송 서비스(emailService)에서 이메일 코드 전송을 처리.
    이메일 유형에 따라 코드 전송 (admin = 1, member = 2)
    ----------------------------------------------------------------------------- */
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
    // 이메일 인증 코드 확인
    /* -----------------------------------------------------------------------------
    경로 : /member/checkEmailCode (POST)
    인수 : String email, String authenticationCode
    출력 : ResponseEntity<String> (인증 결과)
    설명 : 사용자가 입력한 인증 코드와 이메일을 비교하여 인증 여부를 확인. 인증 성공 여부를 반환.
    ----------------------------------------------------------------------------- */
    @PostMapping("/checkEmailCode")
    @ResponseBody
    public ResponseEntity<String> CheckEmailCode(@RequestParam("email") String email, @RequestParam("authenticationCode") String authenticationCode){
        try {
            log.info("123");
            String result = emailService.checkEmailCode(email, authenticationCode)+"";
            return ResponseEntity.ok().body(result);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("에러 발생");
        }
    }


}
