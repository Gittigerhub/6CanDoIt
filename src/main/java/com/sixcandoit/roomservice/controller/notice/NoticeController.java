package com.sixcandoit.roomservice.controller.notice;

import com.sixcandoit.roomservice.dto.notice.NoticeDTO;
import com.sixcandoit.roomservice.service.notice.NoticeService;
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

@Controller // get, post 매핑만 사용이 가능
@RequiredArgsConstructor
@Log

public class NoticeController {

    private final NoticeService noticeService;


    //return중에서 중복되는 부분을 찾아서 맵핑이름과 동일한 부분이 기준
    // 전체 목록 페이지(기준)
    @GetMapping("/notice/list")
    public String list(Model model){
        log.info("모든 데이터를 읽어온다...");
        List<NoticeDTO> noticeDTOList = noticeService.list();

        model.addAttribute("noticeDTOList", noticeDTOList);
        return "notice/list";
    }
    // 추가버튼을 클릭했을 때 입력폼 페이지
    @GetMapping("/notice/register")
    public String register(Model model){
        log.info("입력폼 페이지 이동...");

        model.addAttribute("noticeDTO", new NoticeDTO());

        return "notice/register";
    }
    // 입력폼에서 저장버튼을 클릭했을 때 저장 처리
    // @ModelAttribute : HTML의 내용을 DTO에 저장
    @PostMapping("/notice/register")
    public String registerProc(@Valid @ModelAttribute NoticeDTO noticeDTO,
                               BindingResult bindingResult){
        log.info("입력폼 내용을 저장");

        if (bindingResult.hasErrors()){
            log.info("유효성 검사 오류 발생");
            return "notice/register";
        }
        //유효성 검사 성공 시 등록 처리
        noticeService.register(noticeDTO);

        return "redirect:/notice/list";
    }
    // 목록에서 상세버튼을 클릭했을 때 상세보기 처리 후 데이터를 페이지 전달
    // Form으로 전달시에는 get, post로 전달가능
    // 그외는 get 전달
    @GetMapping("/notice/read")
    public String read(Model model){
        log.info("개별읽기...");
       NoticeDTO noticeDTO = noticeService.read(1);
        model.addAttribute("noticeDTO", noticeDTO);

        return "notice/read";
    }
    // 목록에서 수정버튼을 클릭했을 때 수정폼 페이지
    @GetMapping("/notice/update")
    public String update(@RequestParam Integer idx, Model model) {
        log.info("수정할 데이터 읽기...");
        NoticeDTO noticeDTO = noticeService.read(idx);

        log.info("개별 데이터를 페이지로; 전달합니다.");
        model.addAttribute("noticeDTO", noticeDTO);

        return "notice/update";
    }
    
    // 수정폼에서 수정버튼을 클릭했을 때 수정 처리
    @PostMapping("/notice/update")
    public String updateProc( @Valid @ModelAttribute NoticeDTO noticeDTO,
                              BindingResult bindingResult){
        log.info("수정된 데이터 저장...");

        if (bindingResult.hasErrors()) {//유효성 검사 실패 시
            log.info("유효성 검사 오류 발생");
            return "notice/update";
        }
        //유효성 검사 성공시
        noticeService.update(noticeDTO);
            return "redirect:/notice/list";

    }

    @GetMapping("/notice/delete")
    public String delete(@RequestParam Integer idx){
        log.info("삭제 처리...");
        noticeService.delete(idx);

        return "redirect:/notice/list";
    }
}

