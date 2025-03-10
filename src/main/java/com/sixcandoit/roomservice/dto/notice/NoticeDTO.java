package com.sixcandoit.roomservice.dto.notice;


import com.sixcandoit.roomservice.dto.member.MemberDTO;
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

public class NoticeDTO {

    private Integer idx;            // 공지사항 키

    @NotBlank(message = "제목을 입력하세요")
    @Size(min=1, max = 30, message = "제목은 최대  30자까지 작성 가능합니다.")
    private String noticeTitle;     // 제목

    @NotBlank(message = "내용을 입력하세요.")
    @Size(min = 1, message = "내용은 공백일 수 없습니다.")
    private String noticeContents;  // 내용

    private String noticeType;      // 공지타입

    private int noticeHits;         // 조회수

    private LocalDateTime insDate;  // 작성일

    private LocalDateTime modDate;  // 수정일
    
    private MemberDTO memberDTO;

    public NoticeDTO setMemberDTO(MemberDTO memberDTO){
        this.memberDTO =memberDTO;
        return this;
        
    }
    private String memberName;

}