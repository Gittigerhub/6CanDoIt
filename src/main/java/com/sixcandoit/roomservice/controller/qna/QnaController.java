package com.sixcandoit.roomservice.controller.qna;

import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.service.qna.QnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log
public class QnaController {
    // final 선언
    private final QnaService qnaService;

    // Qna의 Q 전체 목록
    // DB에서 모든 데이터 화면에 출력
    @GetMapping("/qna/list")
    public String list(Model model){
        log.info("모든 데이터를 읽어옵니다.");
        List<QnaDTO> qnaDTOList = qnaService.list();

        model.addAttribute("qnaDTOList", qnaDTOList);
        return "qna/list";
    }

    // Qna의 Q 등록
    @GetMapping("/qna/register")
    public String register(Model model){
        log.info("질문 페이지로 이동합니다.");

        model.addAttribute("qnaDTO", new QnaDTO()); // 빈 QnaDTO 객체 전달

        return "qna/register";
    }

    // Qna의 Q 등록 저장 처리
    @PostMapping("/qna/register")
    public String registerProc(@Valid @ModelAttribute QnaDTO qnaDTO,
                               BindingResult bindingResult){
        log.info("질문한 내용을 저장합니다.");

        if (bindingResult.hasErrors()){ // 유효성 검사에 실패 시
            log.info("유효성 검사 오류 발생");
            return "qna/register"; // register로 돌아간다
        }
        // 유효성 검사 성공 시 등록 처리
        qnaService.register(qnaDTO);

        return "redirect:/qna/list";
    }

    // Qna의 Q 읽기
    @GetMapping("/qna/read")
    public String read(@RequestParam Integer idx, Model model){
        log.info("개별 데이터를 읽는 중입니다.");
        QnaDTO qnaDTO = qnaService.read(idx);

        log.info("개별 데이터를 페이지에 전달하는 중입니다.");
        model.addAttribute("qnaDTO", qnaDTO);

        return "qna/read";
    }

    // Qna의 Q 수정할 게시글 불러오기
    @GetMapping("/qna/update")
    public String update(@RequestParam Integer idx, Model model){
        log.info("수정할 데이터를 읽는 중입니다.");
        QnaDTO qnaDTO = qnaService.read(idx);

        log.info("개별 데이터를 페이지로 전달합니다.");
        model.addAttribute("qnaDTO", qnaDTO);

        return "qna/update";
    }

    // Qna의 Q 수정할 게시글 수정하기
    @PostMapping("/qna/update")
    public String updateProc(@Valid @ModelAttribute QnaDTO qnaDTO,
                             BindingResult bindingResult){
        log.info("수정된 데이터를 저장합니다.");

        if (bindingResult.hasErrors()){ // 유효성 검사에 실패 시
            log.info("유효성 검사 오류 발생");
            return "qna/update"; // update로 돌아간다
        }
        // 유효성 검사 성공 시 수정 처리
        qnaService.update(qnaDTO);

        return "redirect:/qna/list";
    }

    // Qna의 Q 삭제
    @GetMapping("/qna/delete")
    public String delete(@RequestParam Integer idx){
        log.info("데이터를 삭제합니다.");
        qnaService.delete(idx);

        return "redirect:/qna/list";
    }
}
