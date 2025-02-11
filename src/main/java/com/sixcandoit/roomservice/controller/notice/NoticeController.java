package com.sixcandoit.roomservice.controller.notice;

import com.sixcandoit.roomservice.dto.notice.NoticeDTO;

import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.service.notice.NoticeService;

import com.sixcandoit.roomservice.util.PageNationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@Controller // get, post 매핑만 사용이 가능
@RequiredArgsConstructor
@Log

public class NoticeController {

    private final NoticeService noticeService;
    private final MemberRepository memberRepository;


    // Qna의 Q 전체 목록, 페이지, 키워드로 분류 검색
    // 페이지 번호를 받아서 해당 페이지의 데이터 조회하여 목록 페이지로 전달
    @GetMapping("/notice/list")
    public String list(@PageableDefault(page = 1) Pageable page, // 페이지 정보
                       @RequestParam(value = "type", defaultValue = "") String type, // 검색대상
                       @RequestParam(value = "keyword", defaultValue = "") String keyword, // 키워드
                       Model model){
        // 해당페이지의 내용을 서비스를 통해 데이터베이스로부터 조회

        Page<NoticeDTO> noticeDTOS = noticeService.noticeList(page, type, keyword);
        // html에 필요한 페이지 정보를 받는다.
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(noticeDTOS);

        model.addAttribute("noticeDTO", noticeDTOS); // 데이터 전달
        model.addAllAttributes(pageInfo); // 페이지 정보
        model.addAttribute("type", type); //검색분류
        model.addAttribute("keyword", keyword); // 키워드

        return "notice/list";
    }

    // Qna의 Q 등록
    @GetMapping("/notice/register")
    public String register (Model model){
        log.info("질문 페이지로 이동합니다.");

        model.addAttribute("noticeDTO", new NoticeDTO());

        return "notice/register";
    }

    // Qna의 Q 등록 저장 처리
    @PostMapping("/notice/register")
    public String registerProc(@Valid @ModelAttribute NoticeDTO noticeDTO,
                               BindingResult bindingResult) {
        log.info("질문한 내용을 저장합니다.");

        if (bindingResult.hasErrors()) { // 유효성 검사에 실패 시
            log.info("유효성 검사 오류 발생");
            return "notice/register"; // register로 돌아간다
        }

        // 유효성 검사 성공 시 등록 처리
        noticeService.noticeRegister(noticeDTO);

        return "redirect:/notice/list";
    }


    @GetMapping("/notice/read")
    public String read(@RequestParam("idx") Integer idx, Model model) {
        noticeService.count(idx);
        log.info("개별 데이터를 읽는 중입니다");
        NoticeDTO noticeDTO = noticeService.noticeRead(idx);

        log.info("개별 데이터를 페이지에 전달하는 중입니다");
        model.addAttribute("noticeDTO",noticeDTO);

        return "notice/read";
    }

    @GetMapping("/notice/update")
    public String update(@RequestParam("idx") Integer idx, Model model) {
        log.info("수정할 데이터를 읽는 중입니다.");
        NoticeDTO noticeDTO = noticeService.noticeRead(idx);

        log.info("개별 데이터를 페이지로 전달합니다.");
        model.addAttribute("noticeDTO", noticeDTO);

        return "notice/update";
    }


    @PostMapping("/notice/update")
    public String updateProc(@Valid @ModelAttribute NoticeDTO noticeDTO,
                             BindingResult bindingResult) {
        log.info("수정된 데이터를 저장합니다.");

        if (bindingResult.hasErrors()) { // 유효성 검사에 실패 시
            log.info("유효성 검사 오류 발생");
            return "notice/update"; // update로 돌아간다
        }
        // 유효성 검사 성공 시 수정 처리
        noticeService.noticeUpdate(noticeDTO);

        return "redirect:/notice/list";
    }

    // Qna의 Q 삭제
    @GetMapping("/notice/delete")
    public String delete(@RequestParam("idx") Integer idx) {
        log.info("데이터를 삭제합니다.");
        noticeService.noticeDelete(idx);

        return "redirect:/notice/list";
    }
}