package com.sixcandoit.roomservice.controller.qna;

import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.dto.qna.ReplyDTO;
import com.sixcandoit.roomservice.service.qna.QnaService;
import com.sixcandoit.roomservice.service.qna.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reply") //url 프리픽스를 /reply로 고정
@Controller
@RequiredArgsConstructor
@Log
public class ReplyController {

    private final ReplyService replyService;
    private final QnaService qnaService;

    // Qna의 A 읽기
    // Qna의 A 등록
    @PostMapping("/register/{idx}")
    public String register(@RequestParam Integer qnaIdx,
                           @ModelAttribute ReplyDTO replyDTO,
                           BindingResult bindingResult,
                           Model model){
        if (bindingResult.hasErrors()){
            // 오류가 있으면 질문 페이지로 돌아간다.
            model.addAttribute("qnaDTO", qnaService.qnaRead(qnaIdx));
            return "qna/read?idx=" + qnaIdx;
        }
        // 답변 등록 처리
        replyService.replyRegister(replyDTO);

        return "redirect:/qna/read?idx=" + qnaIdx;
    }
    // Qna의 A 수정

    // Qna의 A 삭제


}
