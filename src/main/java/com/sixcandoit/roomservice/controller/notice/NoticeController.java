package com.sixcandoit.roomservice.controller.notice;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.notice.NoticeDTO;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.notice.NoticeService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Map;

@Controller // get, post 매핑만 사용이 가능
@RequiredArgsConstructor
@Log4j2

public class NoticeController {

    private final NoticeService noticeService;
    private final ImageFileService  imageFileService;
    private final ModelMapper modelMapper;


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
    //사용자 공지사항 목록 페이지
    @GetMapping("/notice/userlist")
    public String userList(@PageableDefault(page = 1) Pageable page,
                           @RequestParam(value = "type",defaultValue = "")String type,
                           @RequestParam(value = "keyword",defaultValue = "")String keyword,
                           Model model          ){
        Page<NoticeDTO> noticeDTOS = noticeService.noticeList(page, type, keyword);
        System.out.println("Notice List:" + noticeDTOS.getContent());

        //페이지네이션 정보
        Map<String,Integer> pageInfo=PageNationUtil.Pagination(noticeDTOS);
        System.out.println("Page Info:"+pageInfo);

        //사용자에게 전달할 데이터
        model.addAttribute("noticeDTO", noticeDTOS);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageInfo", pageInfo);

        return "notice/userlist";
    }

    @GetMapping("/notice/userread")
    public String userRead(@RequestParam("idx")Integer idx,Model model){

        noticeService.count(idx);//조회수 증가

        //공지사항 상세 내용
        NoticeDTO noticeDTO=noticeService.noticeRead(idx);

        //이미지 조회
        List<ImageFileDTO> imageFileDTOS=imageFileService.readImage(idx, "notice");

        //대표 이미지 여부 확인
        boolean hasRepImage =imageFileDTOS.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

        //모델에 공지사항 데이터 전달
        model.addAttribute("noticeDTO", noticeDTO);
        model.addAttribute("hasRepImage",hasRepImage);
        model.addAttribute("imageFileDTOS",imageFileDTOS);
        return "notice/userread";
    }

    @GetMapping("/notice/register")
    public String register (Model model){
        log.info("질문 페이지로 이동합니다.");

        model.addAttribute("noticeDTO", new NoticeDTO());

        return "notice/register";
    }


    @PostMapping("/notice/register")

    public String registerProc(@Valid @ModelAttribute NoticeDTO noticeDTO,
                               BindingResult bindingResult,
                               List<MultipartFile> imageFiles) {

        log.info("질문한 내용을 저장합니다.");

        if (bindingResult.hasErrors()) {        // 유효성 검사에 실패 시
            log.info("유효성 검사 오류 발생");
            return "notice/register";           // register로 돌아간다
        }

        // 유효성 검사 성공 시 등록 처리
        noticeService.noticeRegister(noticeDTO,imageFiles);

        return "redirect:/notice/list";
    }


    @GetMapping("/notice/read")
    public String read(@RequestParam("idx") Integer idx, Model model) {

        String join="notice";

        noticeService.count(idx);

        log.info("개별 데이터를 읽는 중입니다");
        NoticeDTO noticeDTO = noticeService.noticeRead(idx);

        // 이미지 조회
        List<ImageFileDTO> imageFileDTOS=
                imageFileService.readImage(idx ,join);

        boolean hasRepImage = imageFileDTOS.stream()
                .anyMatch(imageFileDTO->"Y".equals(imageFileDTO.getRepimageYn()));

        log.info("개별 데이터를 페이지에 전달하는 중입니다");
        model.addAttribute("noticeDTO", noticeDTO);
        model.addAttribute("hasRepImage", hasRepImage);
        model.addAttribute("imageFileDTOS", imageFileDTOS);


        return "notice/read";
    }

    @GetMapping("/notice/update")
    public String update(@RequestParam("idx") Integer idx, Model model) {

        String join="notice";

        log.info("수정할 데이터를 읽는 중입니다.");
        NoticeDTO noticeDTO = noticeService.noticeRead(idx);

        log.info("개별 데이터를 페이지로 전달합니다.");
        model.addAttribute("noticeDTO", noticeDTO);

        return "notice/update";
    }


    @PostMapping("/notice/update")
    public String updateProc(@Valid @ModelAttribute NoticeDTO noticeDTO,
                             String join, List<MultipartFile> imageFiles) {

        log.info("수정된 데이터를 저장합니다.");

        try {
            if (imageFiles != null && !imageFiles.isEmpty()) {
                MultipartFile firstFile = imageFiles.get(0);  // 첫 번째 파일 가져오기
                log.info("0번 인덱스 파일 이름: {}", firstFile.getOriginalFilename());  // 파일 이름 출력
            } else {
                log.info("imageFiles 리스트가 비어 있습니다.");
            }
            noticeService.noticeUpdate(noticeDTO, join, imageFiles);
            return "redirect:/notice/read?idx=" + noticeDTO.getIdx();

        } catch (Exception e) {
            log.info(e.getMessage());
            return "notice/update";
        }
    }


    @PostMapping("/notice/delete/{idx}")
    public String delete(@PathVariable("idx") Integer idx) {
        String join = "notice";
        log.info("데이터를 삭제합니다.");
        noticeService.noticeDelete(idx, join);
        return "redirect:/notice/list";
    }


    public ModelMapper getModelMapper() {
        return modelMapper;
    }
}