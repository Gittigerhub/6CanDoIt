package com.sixcandoit.roomservice.dto.qna;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {

    private Integer idx;                 // 기본 키
    private Integer qnaIdx;             // 관련된 Qna 게시글의 idx
    private String replyTitle;           // 제목
    private String replyContents;        // 내용
    private LocalDateTime insDate;      // 작성 일자
    private LocalDateTime modDate;      // 수정 일자

    private QnaDTO qnaDTO;
}
