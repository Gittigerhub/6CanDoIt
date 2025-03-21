package com.sixcandoit.roomservice.dto.qna;

import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.dto.admin.AdminDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class QnaDTO {

    private Integer idx;                // 게시글 번호

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
    private String favYn;              // 자주 묻는 질문 설정 (Y: 활성, N: 비활성)
    private String replyYn;            // 답변 여부 (Y: 답변완료, N: 미답변)

    private MemberDTO memberDTO;       // Member 참조

    private AdminDTO adminJoin;  // adminIdx 대신 adminJoin 사용

    private String memberName;         // 작성자 이름

    private Long replyCount;

    public QnaDTO setMemberDTO(MemberDTO memberDTO){
        this.memberDTO = memberDTO;
        return this;
    }
}