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
public class QnaDTO {

    private Integer idx;                // 기본 키
    private String qnaTitle;           // 제목
    private String qnaContents;        // 내용
    private int qnaHits;               // 조회 수
    private LocalDateTime insDate;      // 작성 일자
    private LocalDateTime modDate;      // 수정 일자

}
