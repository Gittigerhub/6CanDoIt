package com.sixcandoit.roomservice.dto.qna;

import com.sixcandoit.roomservice.dto.admin.AdminDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "제목을 입력하세요.")
    @Size(min = 1, max = 30, message = "제목은 최대 30자까지 작성 가능합니다.")
    private String replyTitle;           // 제목

    @NotBlank(message = "내용을 입력하세요.")
    @Size(min = 1, message = "내용은 공백일 수 없습니다.")
    private String replyContents;        // 내용

    private LocalDateTime insDate;      // 작성 일자
    private LocalDateTime modDate;      // 수정 일자

    private QnaDTO qnaDTO;
    
    private AdminDTO adminJoin;         // 답변 작성 관리자
}
