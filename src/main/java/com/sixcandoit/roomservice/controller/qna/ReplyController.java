package com.sixcandoit.roomservice.controller.qna;

import com.sixcandoit.roomservice.config.CustomUserDetails;
import com.sixcandoit.roomservice.dto.qna.ReplyDTO;
import com.sixcandoit.roomservice.service.qna.QnaService;
import com.sixcandoit.roomservice.service.qna.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Log
public class ReplyController {

    private final ReplyService replyService;
    private final QnaService qnaService;

    // Qna의 A 읽기 -> Q 상세보기에서 목록 출력
    // Qna의 A 등록 -> 모달 처리
    @PostMapping("/reply/register")
    public String register(@Valid @RequestParam Integer qnaIdx,
                           @ModelAttribute ReplyDTO replyDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "오류");
            redirectAttributes.addFlashAttribute("alertMessage", "입력값이 올바르지 않습니다.");
            return "redirect:/qna/adminread?idx=" + qnaIdx;
        }
        try {
            // 답변 등록 처리
            replyService.replyRegister(replyDTO);
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "success");
            redirectAttributes.addFlashAttribute("alertTitle", "성공");
            redirectAttributes.addFlashAttribute("alertMessage", "답변이 등록되었습니다.");
            return "redirect:/qna/adminread?idx=" + qnaIdx;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "오류");
            redirectAttributes.addFlashAttribute("alertMessage", "답변 등록에 실패했습니다.");
            return "redirect:/qna/adminread?idx=" + qnaIdx;
        }
    }

    // Qna의 A 수정 처리
    @PostMapping("/reply/update")
    public String update(@Valid @RequestParam Integer qnaIdx,
                         @ModelAttribute ReplyDTO replyDTO,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        log.info("수정할 데이터 저장...");

        // 관리자 권한 체크
        if (userDetails == null || !userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().matches("ROLE_(ADMIN|HO|BO)"))) {
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "접근 제한");
            redirectAttributes.addFlashAttribute("alertMessage", "관리자만 답변을 수정할 수 있습니다.");
            return "redirect:/qna/list";
        }

        if (bindingResult.hasErrors()){
            // 오류가 있으면 질문 페이지로 돌아간다.
            model.addAttribute("qnaDTO", qnaService.qnaRead(qnaIdx));
            return "redirect:/qna/adminread?idx=" + qnaIdx;
        }
        
        //답변 수정 처리
        replyService.replyUpdate(replyDTO);

        return "redirect:/qna/adminread?idx=" + qnaIdx;
    }

    // Qna의 A 삭제
    @GetMapping("/reply/delete")
    public String delete(@RequestParam Integer idx, 
                        @RequestParam Integer qnaIdx,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        
        // 관리자 권한 체크
        if (userDetails == null || !userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().matches("ROLE_(ADMIN|HO|BO)"))) {
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "접근 제한");
            redirectAttributes.addFlashAttribute("alertMessage", "관리자만 답변을 삭제할 수 있습니다.");
            return "redirect:/qna/list";
        }

        log.info("idx가 null인지 확인 중...");
        if (idx == null) {
            // idx가 null인 경우 에러 처리
            return "redirect:/error";
        }

        log.info("데이터를 삭제합니다.");
        // 답변 삭제
        replyService.replyDelete(idx);

        // 삭제 후 해당 질문 페이지로 리다이렉트
        log.info("질문 페이지로 리다이렉트하는 중...");
        if (qnaIdx == null) {
            return "redirect:/error";
        }
        return "redirect:/qna/adminread?idx=" + qnaIdx;
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute ReplyDTO replyDTO,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        // 관리자 권한 체크
        if (!userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "권한 없음");
            redirectAttributes.addFlashAttribute("alertMessage", "관리자만 답변을 등록할 수 있습니다.");
            return "redirect:/qna/adminread?idx=" + replyDTO.getQnaIdx();
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "입력 오류");
            redirectAttributes.addFlashAttribute("alertMessage", "답변 내용을 입력해주세요.");
            return "redirect:/qna/adminread?idx=" + replyDTO.getQnaIdx();
        }

        replyService.replyRegister(replyDTO);
        redirectAttributes.addFlashAttribute("sweetAlert", true);
        redirectAttributes.addFlashAttribute("alertType", "success");
        redirectAttributes.addFlashAttribute("alertTitle", "성공");
        redirectAttributes.addFlashAttribute("alertMessage", "답변이 등록되었습니다.");
        return "redirect:/qna/adminread?idx=" + replyDTO.getQnaIdx();
    }
}
