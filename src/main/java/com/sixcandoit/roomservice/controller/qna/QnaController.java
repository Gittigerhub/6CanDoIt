package com.sixcandoit.roomservice.controller.qna;

import com.sixcandoit.roomservice.config.CustomUserDetails;
import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.repository.qna.QnaRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.qna.QnaService;
import com.sixcandoit.roomservice.service.qna.ReplyService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final ModelMapper modelMapper;
    private final QnaRepository qnaRepository;

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
    public String qnalist(@PageableDefault(page = 1) Pageable page,
                       @RequestParam(value = "type", defaultValue = "") String type,
                       @RequestParam(value = "keyword", defaultValue = "") String keyword,
                       @AuthenticationPrincipal CustomUserDetails userDetails,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        
        // 관리자 권한 체크
        if (userDetails == null || !userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().matches("ROLE_(ADMIN|HO|BO)"))) {
            log.warn("관리자 권한이 없는 사용자의 접근 시도");
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "접근 제한");
            redirectAttributes.addFlashAttribute("alertMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/qna/list";
        }

        Page<QnaDTO> qnaDTOS;
        
        // 내가 답변한 게시글 필터링
        if ("8".equals(type)) {
            log.info("내가 답변한 게시글 필터링 시작 - 관리자 IDX: {}", userDetails.getAdmin().getIdx());
            qnaDTOS = qnaService.getQnaListByAdmin(userDetails.getAdmin().getIdx(), page);
            log.info("필터링된 게시글 수: {}", qnaDTOS.getTotalElements());
            log.info("필터링된 게시글 내용:");
            qnaDTOS.getContent().forEach(dto -> {
                log.info("게시글 IDX: {}, 제목: {}, 작성자: {}, 답변여부: {}, 자주묻는질문여부: {}", 
                    dto.getIdx(), dto.getQnaTitle(), dto.getMemberName(), dto.getReplyYn(), dto.getFavYn());
            });
            // keyword 파라미터 초기화 (검색어 무시)
            keyword = "";
        } else {
            qnaDTOS = qnaService.qnaList(page, type, keyword);
        }

        Map<String, Integer> pageInfo = PageNationUtil.Pagination(qnaDTOS);

        log.info("모델에 데이터 추가 - 총 게시글 수: {}, 현재 페이지: {}, 시작 페이지: {}, 끝 페이지: {}", 
            qnaDTOS.getTotalElements(), pageInfo.get("currentPage"), pageInfo.get("startPage"), pageInfo.get("endPage"));

        model.addAttribute("qnalist", qnaDTOS);
        model.addAllAttributes(pageInfo);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);

        return "qna/qnalist";
    }

    // Qna의 Q 등록
    @GetMapping("/qna/register")
    public String register(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        log.info("질문 페이지로 이동합니다.");
        
        // 인증되지 않은 사용자 체크
        if (userDetails == null) {
            log.error("인증되지 않은 사용자의 접근");
            return "redirect:/member/login";  // 로그인 페이지로 리다이렉트
        }

        log.info("현재 로그인한 사용자: {}", userDetails.getUsername());
        log.info("사용자 권한: {}", userDetails.getAuthorities());

        model.addAttribute("qnaDTO", new QnaDTO()); // 빈 QnaDTO 객체 전달

        return "qna/register";
    }

    // Qna의 Q 등록 저장 처리
    @PostMapping("/qna/register")
    public String registerProc(@Valid @ModelAttribute QnaDTO qnaDTO,
                             BindingResult bindingResult, 
                             List<MultipartFile> imageFiles,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("질문한 내용을 저장합니다.");
        
        // 인증되지 않은 사용자 체크
        if (userDetails == null) {
            log.error("인증되지 않은 사용자의 접근");
            return "redirect:/member/login";  // 로그인 페이지로 리다이렉트
        }

        if (bindingResult.hasErrors()){ // 유효성 검사에 실패 시
            log.info("유효성 검사 오류 발생");
            return "qna/register"; // register로 돌아간다
        }

        try {
            // 현재 로그인한 사용자 정보 설정
            qnaDTO.setMemberName(userDetails.getMember().getMemberName());
            qnaDTO.setMemberDTO(modelMapper.map(userDetails.getMember(), MemberDTO.class));
            
            log.info("작성자 정보 설정 - 이름: {}", qnaDTO.getMemberName());

            // 유효성 검사 성공 시 등록 처리
            qnaService.qnaRegister(qnaDTO, imageFiles);
            
            return "redirect:/qna/list";
        } catch (Exception e) {
            log.error("QnA 등록 중 오류 발생: " + e.getMessage(), e);
            return "qna/register";
        }
    }

    // Qna의 Q 읽기
    @GetMapping("/qna/read")
    public String read(@RequestParam Integer idx, Model model,
                      @AuthenticationPrincipal CustomUserDetails userDetails,
                      RedirectAttributes redirectAttributes){

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
        
        // 현재 로그인한 사용자 정보 추가
        if (userDetails != null) {
            model.addAttribute("currentUser", userDetails.getMember().getMemberName());
        }

        // 일반 회원은 자신의 글이나 자주 묻는 질문만 조회 가능
        if (userDetails != null && 
            !qnaDTO.getMemberName().equals(userDetails.getMember().getMemberName()) &&
            !"Y".equals(qnaDTO.getFavYn())) {
            
            // 관리자인 경우 adminread로 리다이렉트
            if (userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().matches("ROLE_(ADMIN|HO|BO)"))) {
                return "redirect:/qna/adminread?idx=" + idx;
            }
            
            log.warn("접근 제한 - 사용자: {}, 작성자: {}, FavYn: {}", 
                userDetails.getMember().getMemberName(), 
                qnaDTO.getMemberName(), 
                qnaDTO.getFavYn());
            
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "접근 제한");
            redirectAttributes.addFlashAttribute("alertMessage", "자주 묻는 질문이나 자신이 작성한 글만 읽을 수 있습니다.");
            return "redirect:/qna/list";
        }

        return "qna/read";
    }

    // 관리자용 Qna 읽기
    @GetMapping("/qna/adminread")
    public String adminread(@RequestParam Integer idx, Model model,
                      @AuthenticationPrincipal CustomUserDetails userDetails,
                      RedirectAttributes redirectAttributes){

        // 상세 로깅 추가
        if (userDetails != null) {
            log.info("현재 로그인한 사용자: {}", userDetails.getUsername());
            log.info("사용자 권한: {}", userDetails.getAuthorities());
        } else {
            log.warn("로그인하지 않은 사용자의 접근");
        }

        // 관리자 권한 체크
        if (userDetails == null || !userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().matches("ROLE_(ADMIN|HO|BO)"))) {
            log.warn("관리자 권한 체크 실패 - 사용자 정보: {}", 
                userDetails != null ? userDetails.getAuthorities() : "null");
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "접근 제한");
            redirectAttributes.addFlashAttribute("alertMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/qna/list";
        }

        String join = "qna";

        qnaService.count(idx); // 조회수 증가
        log.info("관리자용 개별 데이터를 읽는 중입니다.");
        QnaDTO qnaDTO = qnaService.qnaRead(idx);

        List<ImageFileDTO> imageFileDTOS =
                imageFileService.readImage(idx, join);

        boolean hasRepImage = imageFileDTOS.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

        log.info("개별 데이터를 페이지에 전달하는 중입니다.");
        model.addAttribute("qnaDTO", qnaDTO);
        model.addAttribute("imageFileDTOS", imageFileDTOS);
        model.addAttribute("hasRepImage", hasRepImage);
        
        // 현재 로그인한 사용자 정보 추가 (안전하게 처리)
        try {
            if (userDetails != null && userDetails.getMember() != null) {
                model.addAttribute("currentUser", userDetails.getMember().getMemberName());
            } else {
                model.addAttribute("currentUser", userDetails.getUsername());
            }
        } catch (Exception e) {
            log.warn("사용자 정보 설정 중 오류 발생: {}", e.getMessage());
            model.addAttribute("currentUser", "Unknown");
        }

        return "qna/adminread";
    }

    // Qna의 Q 수정할 게시글 수정하기
    @GetMapping("/qna/update")
    public String update(@RequestParam Integer idx, Model model,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        log.info("QnA 수정 페이지로 이동합니다.");

        // 인증되지 않은 사용자 체크
        if (userDetails == null) {
            log.error("인증되지 않은 사용자의 접근");
            return "redirect:/member/login";
        }

        // 기존 QnA 데이터 조회
        QnaDTO qnaDTO = qnaService.qnaRead(idx);

        // 관리자 권한 체크
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().matches("ROLE_(ADMIN|HO|BO)"));

        // 관리자가 아닌 경우, 작성자 본인 확인
        if (!isAdmin && !qnaDTO.getMemberName().equals(userDetails.getMember().getMemberName())) {
            log.warn("작성자가 아닌 사용자의 수정 시도 - 요청자: {}, 작성자: {}", 
                userDetails.getMember().getMemberName(), qnaDTO.getMemberName());
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "접근 제한");
            redirectAttributes.addFlashAttribute("alertMessage", "자신이 작성한 글만 수정할 수 있습니다.");
            return "redirect:/qna/list";
        }

        // 일반 사용자의 경우 답변이 있으면 수정 불가
        if (!isAdmin && qnaDTO.getReplyList() != null && !qnaDTO.getReplyList().isEmpty()) {
            log.warn("답변이 있는 글 수정 시도 - QnA IDX: {}", idx);
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "수정 불가");
            redirectAttributes.addFlashAttribute("alertMessage", "이미 답변이 작성된 글은 수정할 수 없습니다.");
            return "redirect:/qna/read?idx=" + idx;
        }

        model.addAttribute("qnaDTO", qnaDTO);
        return "qna/update";
    }

    // Qna의 Q 수정 저장 처리
    @PostMapping("/qna/update")
    public String updateProc(@ModelAttribute QnaDTO qnaDTO,
                             String join, List<MultipartFile> imageFiles,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        log.info("수정된 데이터를 저장합니다. QnA IDX: {}", qnaDTO.getIdx());

        try {
            // 기존 QnA 데이터 조회
            QnaDTO existingQna = qnaService.qnaRead(qnaDTO.getIdx());
            log.info("기존 게시글 조회 완료 - 제목: {}, 작성자: {}", existingQna.getQnaTitle(), existingQna.getMemberName());

            // 작성자 본인 확인
            if (!existingQna.getMemberName().equals(userDetails.getMember().getMemberName())) {
                throw new RuntimeException("자신이 작성한 게시글만 수정할 수 있습니다.");
            }

            // 답변이 있는 경우 수정 불가
            if (existingQna.getReplyList() != null && !existingQna.getReplyList().isEmpty()) {
                throw new RuntimeException("이미 답변이 작성된 글은 수정할 수 없습니다.");
            }

            // 기존 데이터 유지
            qnaDTO.setFavYn(existingQna.getFavYn());
            qnaDTO.setReplyYn(existingQna.getReplyYn());
            qnaDTO.setQnaHits(existingQna.getQnaHits());
            qnaDTO.setMemberName(existingQna.getMemberName());
            
            // MemberDTO 설정
            MemberDTO memberDTO = modelMapper.map(userDetails.getMember(), MemberDTO.class);
            qnaDTO.setMemberDTO(memberDTO);

            log.info("작성자에 의한 게시글 수정 시작 - 작성자: {}", qnaDTO.getMemberName());
            qnaService.qnaUpdate(qnaDTO, join, imageFiles);
            log.info("작성자에 의한 게시글 수정 완료");

            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "success");
            redirectAttributes.addFlashAttribute("alertTitle", "수정 완료");
            redirectAttributes.addFlashAttribute("alertMessage", "게시글이 수정되었습니다.");

            return "redirect:/qna/read?idx=" + qnaDTO.getIdx();

        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "수정 실패");
            redirectAttributes.addFlashAttribute("alertMessage", "게시글 수정 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/qna/read?idx=" + qnaDTO.getIdx();
        }
    }

    // Qna의 Q 삭제
    @GetMapping("/qna/delete")
    public String delete(@RequestParam Integer idx,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        String join = "qna";
        
        // 인증되지 않은 사용자 체크
        if (userDetails == null) {
            log.warn("인증되지 않은 사용자의 삭제 시도");
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "접근 제한");
            redirectAttributes.addFlashAttribute("alertMessage", "로그인이 필요합니다.");
            return "redirect:/qna/list";
        }
        
        // QnA 데이터 조회
        QnaDTO qnaDTO = qnaService.qnaRead(idx);
        
        // 관리자 권한 체크
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().matches("ROLE_(ADMIN|HO|BO)"));
                
        if (isAdmin) {
            try {
                // 관리자는 모든 게시글 삭제 가능
                log.info("관리자에 의한 게시글 삭제 - 관리자: {}, QnA IDX: {}", 
                    userDetails.getUsername(), idx);
                    
                // 해당 질문에 달린 답변을 먼저 삭제
                replyService.deleteRepliesByQnaIdx(idx);
                qnaService.qnaDelete(idx, join);
                
                redirectAttributes.addFlashAttribute("sweetAlert", true);
                redirectAttributes.addFlashAttribute("alertType", "success");
                redirectAttributes.addFlashAttribute("alertTitle", "삭제 완료");
                redirectAttributes.addFlashAttribute("alertMessage", "게시글이 삭제되었습니다.");
                
                return "redirect:/qna/qnalist";
            } catch (Exception e) {
                log.error("관리자의 게시글 삭제 중 오류 발생: " + e.getMessage(), e);
                redirectAttributes.addFlashAttribute("sweetAlert", true);
                redirectAttributes.addFlashAttribute("alertType", "error");
                redirectAttributes.addFlashAttribute("alertTitle", "삭제 실패");
                redirectAttributes.addFlashAttribute("alertMessage", "게시글 삭제 중 오류가 발생했습니다.");
                return "redirect:/qna/qnalist";
            }
        }
        
        // 일반 사용자는 자신의 게시글만 삭제 가능
        if (!qnaDTO.getMemberName().equals(userDetails.getMember().getMemberName())) {
            log.warn("권한 없는 사용자의 삭제 시도 - 사용자: {}, 작성자: {}", 
                userDetails.getMember().getMemberName(), 
                qnaDTO.getMemberName());
            
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "접근 제한");
            redirectAttributes.addFlashAttribute("alertMessage", "자신이 작성한 게시글만 삭제할 수 있습니다.");
            
            return "redirect:/qna/list";
        }

        try {
            // 해당 질문에 달린 답변을 먼저 삭제
            replyService.deleteRepliesByQnaIdx(idx);
            
            log.info("데이터를 삭제합니다.");
            qnaService.qnaDelete(idx, join);

            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "success");
            redirectAttributes.addFlashAttribute("alertTitle", "삭제 완료");
            redirectAttributes.addFlashAttribute("alertMessage", "게시글이 삭제되었습니다.");
            
            return "redirect:/qna/list";
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("sweetAlert", true);
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "삭제 실패");
            redirectAttributes.addFlashAttribute("alertMessage", "게시글 삭제 중 오류가 발생했습니다.");
            return "redirect:/qna/list";
        }
    }

    // 자주 묻는 질문을 등록 및 해제
    @GetMapping("/qna/favYn/update")
    public String updateFavYn(@RequestParam Integer idx, @RequestParam String favYn) {
        qnaService.updateFavYn(idx, favYn);
        return "redirect:/qna/adminread?idx=" + idx; // 상세 페이지로 리다이렉트
    }

    // 예외 처리기 추가
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, RedirectAttributes redirectAttributes) {
        // SweetAlert를 위한 메시지 설정
        redirectAttributes.addFlashAttribute("sweetAlert", true);
        redirectAttributes.addFlashAttribute("alertType", "error");
        redirectAttributes.addFlashAttribute("alertTitle", "오류 발생");
        redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
        return "redirect:/qna/list";
    }
}