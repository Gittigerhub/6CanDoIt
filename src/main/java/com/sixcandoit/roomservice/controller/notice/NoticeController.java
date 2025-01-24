package com.sixcandoit.roomservice.controller.notice;


import com.sixcandoit.roomservice.dto.notice.NoticeDTO;
import com.sixcandoit.roomservice.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller // get, post 매핑만 사용이 가능
//@RestController // create, modify, remove, get, put
@RequiredArgsConstructor
@Log // System.ot.println을 대체

public class NoticeController {
    private final NoticeService noticeService;


    //return중에서 중복되는 부분을 찾아서 맵핑이름과 동일한 부분이 기준
    // 전체 목록 페이지(기준)
    @GetMapping("/notice/list")
    public String listForm(Model model){
        log.info("모든 데이터를 읽어온다...");
        List<NoticeDTO> noticeDTOList = noticeService.list();

        model.addAttribute("noticeDTOList", noticeDTOList);
        return "notice/list";
    }
    // 추가버튼을 클릭했을 때 입력폼 페이지
    @GetMapping("/notice/register")
    public String registerForm(){
        log.info("입력폼 페이지 이동...");

        return "notice/register";
    }
    // 입력폼에서 저장버튼을 클릭했을 때 저장 처리
    // @ModelAttribute : HTML의 내용을 DTO에 저장
    @PostMapping("/notice/regitser")
    public String registerProc(@ModelAttribute NoticeDTO noticeDTO){
        log.info("입력폼 내용을 저장...");
        noticeService.register(noticeDTO);

        return "notice/list";
    }
    // 목록에서 상세버튼을 클릭했을 때 상세보기 처리 후 데이터를 페이지 전달
    // Form으로 전달시에는 get, post로 전달가능
    // 그외는 get 전달
    @GetMapping("/notice/read")
    public String readProc(@RequestParam Integer idx, Model model){
        log.info("개별읽기...");
        NoticeDTO noticeDTO = noticeService.read(idx);  // 전달자가 있으면 변수로 받는다.

        log.info("개별정보를 페이지에 전달...");
        model.addAttribute("noticeEntity", noticeDTO);

        return "notice/read";
    }
    // 목록에서 수정버튼을 클릭했을 때 수정폼 페이지
    @GetMapping("/update")
    public String updateForm(@RequestParam Integer idx, Model model){
        log.info("수정할 데이터 읽기...");
        NoticeDTO noticeDTO = noticeService.read(idx);

        log.info("개별정보를 페이지에 전달...");
        model.addAttribute("noticeEntity", noticeDTO);

        return "notice/update";
    }
    // 수정폼에서 수정버튼을 클릭했을 때 수정 처리
    @PostMapping("/notice/update")
    public String updateProc(@ModelAttribute NoticeDTO noticeDTO){
        log.info("수정된 데이터 저장...");
        noticeService.update(noticeDTO);

        return "redirect:/notice/list";
    }
    // 목록에서 삭제버튼을 클릭했을 때 삭제 처리
    //@GetMapping("/delete/{id}") = rest방식
    @GetMapping("/delete")
    public String deleteProc(@RequestParam Integer idx){
        log.info("삭제 처리...");
        noticeService.delete(idx);

        return "redirect:/notice/list";
    }
}

