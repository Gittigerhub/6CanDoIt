package com.sixcandoit.roomservice.controller.qna;

import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.service.qna.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Log
public class QnaController {
    // final 선언
    private final QnaService qnaService;

    // Qna의 Q 등록
    @GetMapping("/register")
    public String register(){
        log.info("질문 페이지로 이동합니다.");

        return "qna/register";
    }

    // Qna의 Q 등록 저장 처리
    @PostMapping("/register")
    public String registerProc(@ModelAttribute QnaDTO qnaDTO){
        log.info("질문한 내용을 저장합니다.");
        qnaService.register(qnaDTO);

        return "list";
    }

    // Qna의 Q 수정
    @GetMapping("/update")
    public String update(){
        return "qna/update";
    }

    // Qna의 Q 읽기
    @GetMapping("/read")
    public String read(){
        return "qna/read";
    }

    // Qna의 Q 리스트
    @GetMapping("/list")
    public String list(){
        return "qna/list";
    }
}
