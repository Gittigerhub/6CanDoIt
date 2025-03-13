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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
public class NoticeController {

    private final NoticeService noticeService;
    private final ImageFileService imageFileService;
    private final ModelMapper modelMapper;

    @GetMapping("/notice/list")
    public String list(@PageableDefault(page = 1) Pageable page,
                       @RequestParam(value = "type", defaultValue = "") String type,
                       @RequestParam(value = "keyword", defaultValue = "") String keyword,
                       Model model) {
        log.info("관리자 목록 조회 - type: {}, keyword: {}", type, keyword);
        // 관리자용 공지사항 목록
        Page<NoticeDTO> adminNoticeDTOS = noticeService.getNoticeListByType(page, type, keyword, "ADMIN");
        // 사용자용 공지사항 목록도 함께 보여줌
        Page<NoticeDTO> userNoticeDTOS = noticeService.getNoticeListByType(page, type, keyword, "USER");
        
        log.info("관리자 공지 개수: {}, 사용자 공지 개수: {}", 
            adminNoticeDTOS.getTotalElements(), 
            userNoticeDTOS.getTotalElements());
        
        Map<String, Integer> adminPageInfo = PageNationUtil.Pagination(adminNoticeDTOS);
        Map<String, Integer> userPageInfo = PageNationUtil.Pagination(userNoticeDTOS);

        model.addAttribute("adminNoticeDTO", adminNoticeDTOS);
        model.addAttribute("userNoticeDTO", userNoticeDTOS);
        model.addAttribute("adminPageInfo", adminPageInfo);
        model.addAttribute("userPageInfo", userPageInfo);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);

        return "notice/list";
    }

    @GetMapping("/notice/userlist")
    public String userList(@PageableDefault(page = 1) Pageable page,
                           @RequestParam(value = "type",defaultValue = "")String type,
                           @RequestParam(value = "keyword",defaultValue = "")String keyword,
                           Model model) {
        log.info("사용자 목록 조회 - type: {}, keyword: {}", type, keyword);
        // 사용자용 공지사항만 조회
        Page<NoticeDTO> noticeDTOS = noticeService.getNoticeListByType(page, type, keyword, "USER");
        log.info("조회된 사용자 공지 개수: {}", noticeDTOS.getTotalElements());
        
        Map<String,Integer> pageInfo = PageNationUtil.Pagination(noticeDTOS);

        model.addAttribute("noticeDTO", noticeDTOS);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageInfo", pageInfo);

        return "notice/userlist";
    }

    @GetMapping("/notice/read")
    public String read(@RequestParam("idx") Integer idx, Model model) {
        noticeService.count(idx);
        NoticeDTO noticeDTO = noticeService.noticeRead(idx);
        List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(idx, "notice");
        boolean hasRepImage = imageFileDTOS.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

        model.addAttribute("noticeDTO", noticeDTO);
        model.addAttribute("hasRepImage", hasRepImage);
        model.addAttribute("imageFileDTOS", imageFileDTOS);

        return "notice/read";
    }

    @GetMapping("/notice/userread")
    public String userRead(@RequestParam("idx") Integer idx, Model model) {
        noticeService.count(idx);
        NoticeDTO noticeDTO = noticeService.noticeRead(idx);
        List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(idx, "notice");
        boolean hasRepImage = imageFileDTOS.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

        model.addAttribute("noticeDTO", noticeDTO);
        model.addAttribute("hasRepImage", hasRepImage);
        model.addAttribute("imageFileDTOS", imageFileDTOS);
        return "notice/userread";
    }

    @GetMapping("/notice/register")
    public String register(Model model) {
        model.addAttribute("noticeDTO", new NoticeDTO());
        return "notice/register";
    }

    @PostMapping("/notice/register")
    public String registerProc(@Valid @ModelAttribute NoticeDTO noticeDTO,
                               BindingResult bindingResult,
                               List<MultipartFile> imageFiles) {
        log.info("공지사항을 저장합니다. 타입: {}", noticeDTO.getNoticeType());

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 오류 발생");
            return "notice/register";
        }

        noticeService.noticeRegister(noticeDTO, imageFiles);

        return "redirect:/notice/list";
    }

    @GetMapping("/notice/update")
    public String update(@RequestParam("idx") Integer idx, Model model) {
        NoticeDTO noticeDTO = noticeService.noticeRead(idx);
        log.info("수정 페이지 진입 - 공지사항 번호: {}, 타입: {}", idx, noticeDTO.getNoticeType());
        model.addAttribute("noticeDTO", noticeDTO);
        return "notice/update";
    }

    @PostMapping("/notice/update")
    public String updateProc(@Valid @ModelAttribute NoticeDTO noticeDTO,
                             String join,
                             List<MultipartFile> imageFiles) {
        try {
            log.info("공지사항 수정 시작 - 번호: {}, 제목: {}, 타입: {}", 
                noticeDTO.getIdx(), 
                noticeDTO.getNoticeTitle(), 
                noticeDTO.getNoticeType());
            
            if (imageFiles != null && !imageFiles.isEmpty()) {
                MultipartFile firstFile = imageFiles.get(0);
                log.info("0번 인덱스 파일 이름: {}", firstFile.getOriginalFilename());
            }
            noticeService.noticeUpdate(noticeDTO, "notice", imageFiles);
            log.info("공지사항 수정 완료 - 번호: {}", noticeDTO.getIdx());
            return "redirect:/notice/read?idx=" + noticeDTO.getIdx();
        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생: {}", e.getMessage(), e);
            return "notice/update";
        }
    }

    @PostMapping("/notice/delete/{idx}")
    public String delete(@PathVariable("idx") Integer idx) {
        try {
            noticeService.noticeDelete(idx, "notice");
            return "redirect:/notice/list";
        } catch (Exception e) {
            log.error("공지사항 삭제 중 오류 발생: {}", e.getMessage());
            return "redirect:/notice/list";
        }
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }
}