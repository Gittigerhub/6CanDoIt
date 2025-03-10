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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Page<NoticeDTO> noticeDTOS = noticeService.noticeList(page, type, keyword);
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(noticeDTOS);

        model.addAttribute("noticeDTO", noticeDTOS);
        model.addAllAttributes(pageInfo);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);

        return "notice/list";
    }

    @GetMapping("/notice/userlist")
    public String userList(@PageableDefault(page = 1) Pageable page,
                           @RequestParam(value = "type",defaultValue = "")String type,
                           @RequestParam(value = "keyword",defaultValue = "")String keyword,
                           Model model) {
        Page<NoticeDTO> noticeDTOS = noticeService.noticeList(page, type, keyword);
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

        log.info("질문한 내용을 저장합니다.");

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 오류 발생");
            return "notice/register";
        }

        noticeService.noticeRegister(noticeDTO,imageFiles);

        return "redirect:/notice/list";
    }

    @GetMapping("/notice/update")
    public String update(@RequestParam("idx") Integer idx, Model model) {
        NoticeDTO noticeDTO = noticeService.noticeRead(idx);
        model.addAttribute("noticeDTO", noticeDTO);
        return "notice/update";
    }

    @PostMapping("/notice/update")
    public String updateProc(@Valid @ModelAttribute NoticeDTO noticeDTO,
                           String join,
                           List<MultipartFile> imageFiles) {
        try {
            if (imageFiles != null && !imageFiles.isEmpty()) {
                MultipartFile firstFile = imageFiles.get(0);
                log.info("0번 인덱스 파일 이름: {}", firstFile.getOriginalFilename());
            }
            noticeService.noticeUpdate(noticeDTO, "notice", imageFiles);
            return "redirect:/notice/read?idx=" + noticeDTO.getIdx();
        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생: {}", e.getMessage());
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