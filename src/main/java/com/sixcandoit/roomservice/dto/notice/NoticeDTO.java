package com.sixcandoit.roomservice.dto.notice;


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

    private Integer idx; //공지사항 키

    private String noticeTitle; //제목

    @NotBlank(message = "내용을 입력하세요.")
    @Size(min = 1, message = "내용은 공백일 수 없습니다.")

    private String noticeContents; //내용

    private String noticeType; //공지타입

    private int noticeHits; //조회수

    private LocalDateTime insDate;//작성일

    private LocalDateTime modDate;//수정일

    private String noticeImg; //이미지


    private String noticeUrl;

    private String noticeImageFile;
}
