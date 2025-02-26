package com.sixcandoit.roomservice.controller.qna;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.qna.QnaService;
import com.sixcandoit.roomservice.service.qna.ReplyService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
public class QnaController {

    // final 선언
    private final QnaService qnaService;
    private final ImageFileService imageFileService;
    private final ReplyService replyService;

    // Qna의 Q 전체 목록, 페이지, 키워드로 분류 검색
    // 페이지 번호를 받아서 해당 페이지의 데이터 조회하여 목록 페이지로 전달
    @GetMapping("/qna/list")
    public String list(@PageableDefault(page = 1) Pageable page, // 페이지 정보
                       @RequestParam(value = "type", defaultValue = "") String type, // 검색대상
                       @RequestParam(value = "keyword", defaultValue = "") String keyword, // 키워드
                       Model model){

        // 해당페이지의 내용을 서비스를 통해 데이터베이스로부터 조회
        Page<QnaDTO> qnaDTOS = qnaService.qnaList(page, type, keyword);
        // html에 필요한 페이지 정보를 받는다.
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(qnaDTOS);

        model.addAttribute("qnalist", qnaDTOS); // 데이터 전달
        model.addAllAttributes(pageInfo); // 페이지 정보
        model.addAttribute("type", type); //검색분류
        model.addAttribute("keyword", keyword); // 키워드

        return "qna/list";
    }

    // 관리자용 QnA 전체 목록 페이지
    @GetMapping("/qna/qnalist")
    public String qnalist(@PageableDefault(page = 1) Pageable page, // 페이지 정보
                       @RequestParam(value = "type", defaultValue = "") String type, // 검색대상
                       @RequestParam(value = "keyword", defaultValue = "") String keyword, // 키워드
                       Model model){

        // 해당페이지의 내용을 서비스를 통해 데이터베이스로부터 조회
        Page<QnaDTO> qnaDTOS = qnaService.qnaList(page, type, keyword);
        // html에 필요한 페이지 정보를 받는다.
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(qnaDTOS);

        model.addAttribute("qnalist", qnaDTOS); // 데이터 전달
        model.addAllAttributes(pageInfo); // 페이지 정보
        model.addAttribute("type", type); //검색분류
        model.addAttribute("keyword", keyword); // 키워드

        return "qna/qnalist";
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
                               BindingResult bindingResult, List<MultipartFile> imageFiles) {
        log.info("질문한 내용을 저장합니다.");
        System.out.println(qnaDTO);
        System.out.println(imageFiles);
        if (bindingResult.hasErrors()){ // 유효성 검사에 실패 시
            log.info("유효성 검사 오류 발생");
            return "qna/register"; // register로 돌아간다
        }

        // 유효성 검사 성공 시 등록 처리
        qnaService.qnaRegister(qnaDTO, imageFiles);
        System.out.println(imageFiles);

        return "redirect:/qna/list";
    }

    // Qna의 Q 읽기
    @GetMapping("/qna/read")
    public String read(@RequestParam Integer idx, Model model){

        String join = "qna";

        qnaService.count(idx); // 조회수 증가
        log.info("개별 데이터를 읽는 중입니다.");
        QnaDTO qnaDTO = qnaService.qnaRead(idx);

        List<ImageFileDTO> imageFileDTOS =
                imageFileService.readImage(idx, join);

        boolean hasRepImage = imageFileDTOS.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

        log.info("개별 데이터를 페이지에 전달하는 중입니다.");
        model.addAttribute("qnaDTO", qnaDTO);
        model.addAttribute("imageFileDTOS", imageFileDTOS);
        model.addAttribute("hasRepImage", hasRepImage);

        return "qna/read";
    }

    // Qna의 Q 수정할 게시글 불러오기
    @GetMapping("/qna/update")
    public String update(@RequestParam Integer idx, Model model){
        log.info("수정할 데이터를 읽는 중입니다.");
        QnaDTO qnaDTO = qnaService.qnaRead(idx);

        log.info("개별 데이터를 페이지로 전달합니다.");
        model.addAttribute("qnaDTO", qnaDTO);

        return "qna/update";
    }

    // Qna의 Q 수정할 게시글 수정하기
    @PostMapping("/qna/update")
    public String updateProc(@ModelAttribute QnaDTO qnaDTO,
                             String join, List<MultipartFile> imageFiles) {
        log.info("수정된 데이터를 저장합니다.");
        System.out.println("이미지 파일즈 길이 : " + imageFiles.size());
        try {
            // 서비스 메서드 호출 (수정 처리)
            qnaService.qnaUpdate(qnaDTO, join, imageFiles);
            return "redirect:/qna/read?idx=" + qnaDTO.getIdx();

        } catch (Exception e) {
            // 예외가 발생했을 경우 사용자에게 오류 메시지 전달
            log.error(e.getMessage());
            return "qna/update"; // 수정 페이지로 돌아가면서 오류 메시지 전달
        }
    }

    // Qna의 Q 삭제
    @GetMapping("/qna/delete")
    public String delete(@RequestParam Integer idx){
        String join = "qna";

        // 해당 질문에 달린 답변을 먼저 삭제
        replyService.deleteRepliesByQnaIdx(idx);

        log.info("데이터를 삭제합니다.");
        qnaService.qnaDelete(idx, join);

        return "redirect:/qna/list";
    }

    // 자주 묻는 질문을 등록 및 해제
    @GetMapping("/qna/favYn/update")
    public String updateFavYn(@RequestParam Integer idx, @RequestParam String favYn) {
        qnaService.updateFavYn(idx, favYn);
        return "redirect:/qna/read?idx=" + idx; // 상세 페이지로 리다이렉트
    }
}