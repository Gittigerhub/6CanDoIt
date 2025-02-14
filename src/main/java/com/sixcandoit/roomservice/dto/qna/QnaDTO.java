package com.sixcandoit.roomservice.dto.qna;

import com.sixcandoit.roomservice.dto.member.MemberDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QnaDTO {

    private Integer idx;                // 기본 키

    @NotBlank(message = "제목을 입력하세요.")
    @Size(min = 1, max = 30, message = "제목은 최대 30자까지 작성 가능합니다.")
    private String qnaTitle;           // 제목

    @NotBlank(message = "내용을 입력하세요.")
    @Size(min = 1, message = "내용은 공백일 수 없습니다.")
    private String qnaContents;        // 내용

    private int qnaHits;               // 조회 수
    private LocalDateTime insDate;      // 작성 일자
    private LocalDateTime modDate;      // 수정 일자
    private List<ReplyDTO> replyList;    // 댓글 목록
    private String qnaImg;              // 문의사항 이미지 URL
    private String favYn;              // 자주 묻는 질문 설정 (Y: 활성, N: 비활성)

    private MemberDTO memberDTO;       // Member 참조

    public QnaDTO setMemberDTO(MemberDTO memberDTO){
        this.memberDTO = memberDTO;
        return this;
    }

    private String memberIdx;           // 멤버 idx

    private MultipartFile titleFile;
    private MultipartFile contentFile;
    private List<MultipartFile> Files;
    private List<String> imageUrls;  // 이미지 URL 리스트 추가
    private String repimageYn;      // 대표 이미지 여부

}
