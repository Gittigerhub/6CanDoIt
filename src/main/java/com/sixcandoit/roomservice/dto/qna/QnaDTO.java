package com.sixcandoit.roomservice.dto.qna;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String qnaImg;              // 문의사항 이미지

}
