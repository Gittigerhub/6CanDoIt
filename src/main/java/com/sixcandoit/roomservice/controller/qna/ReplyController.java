package com.sixcandoit.roomservice.controller.qna;

import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.dto.qna.ReplyDTO;
import com.sixcandoit.roomservice.entity.qna.ReplyEntity;
import com.sixcandoit.roomservice.service.qna.QnaService;
import com.sixcandoit.roomservice.service.qna.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log
public class ReplyController {

    private final ReplyService replyService;
    private final QnaService qnaService;

    // Qna의 A 읽기 -> Q 상세보기에서 목록 출력
    // Qna의 A 등록 -> 모달 처리
    @PostMapping("/reply/register")
    public String register(@RequestParam Integer qnaIdx,
                           @ModelAttribute ReplyDTO replyDTO,
                           BindingResult bindingResult,
                           Model model){
        if (bindingResult.hasErrors()){
            // 오류가 있으면 질문 페이지로 돌아간다.
            model.addAttribute("qnaDTO", qnaService.qnaRead(qnaIdx));
            return "redirect:/qna/read?idx=" + qnaIdx;
        }
        // 답변 등록 처리
        replyService.replyRegister(replyDTO);

        return "redirect:/qna/read?idx=" + qnaIdx;
    }

    // Qna의 A 수정 처리
    @PostMapping("/reply/update")
    public String update(@RequestParam Integer qnaIdx,
                         @ModelAttribute ReplyDTO replyDTO,
                         BindingResult bindingResult,
                         Model model) {
        log.info("수정할 데이터 저장...");
        if (bindingResult.hasErrors()){
            // 오류가 있으면 질문 페이지로 돌아간다.
            model.addAttribute("qnaDTO", qnaService.qnaRead(qnaIdx));
            return "redirect:/qna/read?idx=" + qnaIdx;
        }
         //답변 등록 처리
        replyService.replyUpdate(replyDTO);

        return "redirect:/qna/read?idx=" + qnaIdx;

    }

    // Qna의 A 삭제
    @GetMapping("/reply/delete")
    public String delete(@RequestParam Integer idx, Integer qnaIdx) {
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
        return "redirect:/qna/read?idx=" + qnaIdx;
    }
}
