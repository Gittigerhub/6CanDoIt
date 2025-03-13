package com.sixcandoit.roomservice.controller.admin;

import com.sixcandoit.roomservice.constant.Level;
import com.sixcandoit.roomservice.dto.admin.AdminDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.repository.admin.AdminRepository;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.service.EmailService;
import com.sixcandoit.roomservice.service.admin.AdminService;
import com.sixcandoit.roomservice.service.member.MemberService;
import com.sixcandoit.roomservice.service.office.OrganizationService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
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
    private final AdminRepository adminRepository;
    private final OrganizationService organizationService;
    private final EmailService emailService;

    @GetMapping("/")
    public String IndexForm(HttpSession session, AdminDTO adminDTO) {
        log.info("최고 관리자 페이지 진입");
        session.setAttribute("adminName", adminDTO.getAdminName());
        return "index";
    }

//    @GetMapping("/ho")
//    public String hoIndexForm(HttpSession session, AdminDTO adminDTO) {
//        log.info("HO 페이지 진입");
//        session.setAttribute("adminName", adminDTO.getAdminName());
//        return "hoindex";
//    }
//
//    @GetMapping("/bo")
//    public String boIndexForm(HttpSession session, AdminDTO adminDTO) {
//        log.info("BO 페이지 진입");
//        session.setAttribute("adminName", adminDTO.getAdminName());
//        return "boindex";
//    }

    // 로그인
    @GetMapping("/login")
    public String showLoginPage(Model model){
        log.info("로그인 페이지를 내놔라");
        List<OrganizationDTO> organizations = organizationService.getAllOrganizations(); // 호텔명 리스트 가져오기

        log.info("호텔 정보 내놔" + organizations);
        model.addAttribute("organizations", organizations);

        return "admin/sign";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String showLogoutPage(HttpSession session) {
        session.invalidate();
        log.info("로그아웃을 하자!~~");
        return "redirect:/admin/login";
    }

    // 회원 가입
    @GetMapping("/register")
    public String register(Model model) {
        log.info("관리자 회원 가입 접속");
        List<OrganizationDTO> organizations = organizationService.getAllOrganizations(); // 호텔명 리스트 가져오기

        log.info("호텔 정보 내놔" + organizations);
        model.addAttribute("organizations", organizations);

        return "admin/sign";
    }

    @PostMapping("/register")
    public String registerProc(@ModelAttribute AdminDTO adminDTO){

        if (adminService.register(adminDTO) == null){
            return "redirect:/admin/login";
        }

        return "redirect:/admin/login";
    }

    // 이메일 중복 확인
    @PostMapping("/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email) {
        log.info("이메일 중복 확인 진입했어?");
        boolean exists = adminService.checkEmailExistence(email);
        String result = exists? "1" :   "0";
        log.info("이메일이 있어? " + email + exists);

        return result;  // 응답 반환
    }

    // 연락처 중복 확인
    @PostMapping("/checkPhone")
    @ResponseBody
    public String checkPhone(@RequestParam("phone") String phone) {
        log.info("연락처 중복 확인 진입했어?");
        boolean exists = adminService.checkPhoneExistence(phone); // 연락처 중복 여부 확인
        String result = exists ? "1" : "0"; // 중복되면 "1", 아니면 "0" 반환
        log.info("연락처가 있어? " + phone + exists);

        return result;  // 응답 반환
    }

    @PostMapping("/sendEmailCode")
    @ResponseBody
    public ResponseEntity<String> sendEmail(@RequestParam("email") String email){
        log.info("이메일 발송 컨트롤러 진입");

        try {
            emailService.codeEmailSending(email, 1);
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

    // 회원 수정 전 비밀번호 확인 페이지
    @GetMapping("/verify")
    public String ShowPasswordVerificationPage(HttpSession session, Model model){
        log.info("비밀번호 확인 진입 했어?");
        String adminEmail = (String) session.getAttribute("adminEmail");

        if (adminEmail == null) {
            log.warn("세션에 adminEmail 없음! 로그인 확인 필요");
            return "redirect:/admin/login";
        }

        model.addAttribute("adminEmail",adminEmail );
        log.info("어드민 이메일 들어왔니?" + adminEmail);

        return "/admin/verify";
    }

    // 비밀번호 검증 처리
    @PostMapping("/verify")
    public String verifyPassword(@RequestParam String password,
                                 @RequestParam String adminEmail,
                                 HttpSession session){

        log.info("비밀번호 확인 진입 했나요?");
        log.info("어드민 이메일 들어왔니? " + adminEmail);
        log.info("입력된 비밀번호: " + password);

        // 세션에서 adminEmail 가져오기 (필요한 경우)
        if (adminEmail == null || adminEmail.isEmpty()) {
            adminEmail = (String) session.getAttribute("adminEmail");
            log.info("세션에서 가져온 adminEmail: " + adminEmail);
        }

        if (adminEmail == null || adminEmail.isEmpty()) {
            log.warn("adminEmail이 null이거나 비어 있음!");
            return "redirect:/admin/verify?error=email_required"; // 에러 메시지 추가 가능
        }

        // 비밀번호를 DB에서 조회하여 비교
        boolean isPasswordValid = adminService.verifyPassword(password, adminEmail);
        log.info("비번 DB에서 조회해서 비교 된거야?"+ isPasswordValid);

        if (isPasswordValid) {
            // 비밀번호가 맞으면 회원 정보 수정 페이지로 리디렉션
            session.setAttribute("email", adminEmail);  // 이메일을 세션에 저장
            System.out.println("비밀번호가 맞습니다.");
            return "redirect:/admin/modify";  // 수정 페이지로 이동
        } else {
            // 비밀번호가 틀리면 다시 비밀번호 확인 페이지로 돌아가기
            System.out.println("비밀번호가 틀렸습니다.");
            return "redirect:/admin/verify";  // 비밀번호 페이지로 리디렉션
        }
    }

    // 회원 정보 수정
    @GetMapping("/modify")
    public String showModifyPage(HttpSession session, Model model){
        log.info("회원 정보 수정 진입 했니?");
        String adminEmail = (String) session.getAttribute("adminEmail");
        AdminDTO adminDTO = new AdminDTO();

        if (adminEmail != null){
            log.info("관리자 이메일 ? " + adminEmail);
            adminDTO = adminService.read(adminEmail);
        }

        model.addAttribute("adminDTO", adminDTO);
        log.info("회원 정보 수정 들어왔니?" + adminDTO);

        return "admin/modify";
    }

    @PostMapping("/modify")
    public String modifyAdmin(@ModelAttribute AdminDTO adminDTO){
        adminService.modify(adminDTO);
        log.info("회원 정보를 수정해줘!!");

        return "redirect:/admin/";
    }

    // 비밀번호 수정
    @GetMapping("/modifypw")
    public String showModifyPWPage(@RequestParam(value = "error", required = false) String error,
                                   HttpSession session, Model model){
        log.info("비밀번호 변경을 해요!!");

        // 세션에서 관리자 이메일 가져오기
        String adminEmail = (String) session.getAttribute("adminEmail");

        if (adminEmail == null) {
            log.info("로그인을 안 했어요");
            return "redirect:/login"; // 로그인 안 했으면 로그인 페이지로 이동
        }

        // 현재 비밀번호 오류 메시지가 있으면 추가
        if (error != null) {
            model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
        }

        return "admin/modifypw";
    }

    @PostMapping("/modifypw")
    public String modifyPW(@RequestParam String currentPassword,
                           @RequestParam String newPassword,
                           @RequestParam String confirmPassword,
                           HttpSession session, RedirectAttributes redirectAttributes){

        // 현재 로그인한 관리자 이메일 가져오기
        String adminEmail = (String) session.getAttribute("adminEmail");
        log.info("관리자 이메일 들어왔니? " + adminEmail);

        if (adminEmail == null) {
            log.info("로그인을 안했어요");
            return "redirect:/login"; // 로그인 안 했으면 로그인 페이지로 이동
        }

        // 비밀번호 검증 후 변경
        boolean isUpdated = adminService.changePassword(adminEmail, currentPassword, newPassword);

        if (!isUpdated) {
            log.warn("현재 비밀번호가 틀렸습니다!");
            redirectAttributes.addFlashAttribute("error", "true");
            return "redirect:/admin/modifypw";
        }
        log.info("비밀번호 변경 성공 ~");
        redirectAttributes.addFlashAttribute("success", "비밀번호가 변경되었습니다.");
        return "redirect:/admin/";
    }

    // 임시비밀번호 발급
    @GetMapping("/password")
    public String showPasswordPage(){
        return "admin/sign";
    }

    @PostMapping("/password")
    public String modifyPassword(@ModelAttribute AdminDTO adminDTO){
        adminService.passwordSend(adminDTO);
        //스크립트로 출력할 메세지를 전달

        return "redirect:/login";
    }

    // 회원 삭제 (일반 회원)
    @PostMapping("/deleteMember")
    @ResponseBody
    public String deleteMember(@RequestParam Integer idx) {
        log.info("회원 삭제 요청: " + idx);

        boolean isDeleted = memberService.deleteMember(idx);
        return isDeleted ? "success" : "fail";
    }

    // 관리자 삭제 (관리자 계정)
    @PostMapping("/deleteAdmin")
    @ResponseBody
    public String deleteAdmin(@RequestParam Integer idx) {
        log.info("관리자 삭제 요청: " + idx);

        boolean isDeleted = adminService.deleteAdmin(idx);
        return isDeleted ? "success" : "fail";
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
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(memberDTOS);

//        model.addAttribute("member", memberDTOS); // 데이터 전달
        model.addAttribute("members", memberDTOS.getContent()); // 리스트만 전달
        model.addAttribute(pageInfo); // 페이지 정보
        model.addAttribute("type", type); // 검색 분류
        model.addAttribute("keyword", keyword); // 키워드


//        List<MemberEntity> memberList = memberRepository.findAll();
//        model.addAttribute("memberList", memberList);
        return "admin/memberlist";
    }

    // 최고 관리자 회원 목록
    @GetMapping("/adminlist")
    public String showAdminList(@PageableDefault(page = 1) Pageable page, // 페이지 정보
                                 @RequestParam(value = "type", defaultValue = "") String type, // 검색대상
                                 @RequestParam(value = "keyword", defaultValue = "") String keyword, // 키워드
                                 @RequestParam(value = "level", defaultValue = "") Level level,  // 권한
                                 Model model){
        log.info("최고 관리자 회원 목록");
        // 해당 페이지의 내용을 서비스를 통해 데이터베이스로 부터 조회
        Page<AdminDTO> adminDTOS = adminService.adminList(page, type, keyword, level);

        // html에 필요한 페이지 정보를 받는다
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(adminDTOS);

        model.addAttribute("admin", adminDTOS); // 데이터 전달
        model.addAttribute(pageInfo); // 페이지 정보
        model.addAttribute("type", type); // 검색 분류
        model.addAttribute("keyword", keyword); // 키워드
        model.addAttribute("level", level); // 권한

        return "admin/adminlist";
    }

    // 본사 관리자 회원 목록
    @GetMapping("/holist")
    public String showHoList(@PageableDefault(page = 1) Pageable page, // 페이지 정보
                                @RequestParam(value = "type", defaultValue = "") String type, // 검색대상
                                @RequestParam(value = "keyword", defaultValue = "") String keyword, // 키워드
                                @RequestParam(value = "level", defaultValue = "") Level level,  // 권한
                                Model model){
        log.info("본사 관리자 회원 목록");
        // 해당 페이지의 내용을 서비스를 통해 데이터베이스로 부터 조회
        Page<AdminDTO> adminDTOS = adminService.adminList(page, type, keyword, level);

        // html에 필요한 페이지 정보를 받는다
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(adminDTOS);

        model.addAttribute("admin", adminDTOS); // 데이터 전달
        model.addAttribute(pageInfo); // 페이지 정보
        model.addAttribute("type", type); // 검색 분류
        model.addAttribute("keyword", keyword); // 키워드
        model.addAttribute("level", level); // 권한

        return "admin/holist";
    }

    // 관리자 권한 변경 처리
    @PostMapping("/updateRole")
    @ResponseBody
    public String updateAdminRole(@RequestParam("idx") Integer idx, @RequestParam("level") Level level, Principal principal) {
        log.info("관리자 권한 변경을 하자~");
        String loggedInAdminEmail = principal.getName();
        AdminEntity loggedInAdmin = adminService.findByAdminEmail(loggedInAdminEmail);
        log.info("이메일 들어왔어?" + loggedInAdmin);
         if (!adminService.canUpdateRole(loggedInAdmin.getLevel(),adminRepository.findById(idx).get().getLevel())) {
            log.info("권한이 없어...");
        return "unauthorized"; // 권한 없음
    }

    boolean success = adminService.updateAdminRole(adminRepository.findById(idx).get().getAdminEmail(), level);
        log.info("권한이 있네 변경했니....?" + success);

        return success ? "success" : "fail";
    }



}
