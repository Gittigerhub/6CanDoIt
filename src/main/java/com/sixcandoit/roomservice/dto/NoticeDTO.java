package com.sixcandoit.roomservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class NoticeDTO {
    private Integer idx; //공지사항 키
    private String  noticeTitle; //제목
    private String  noticeContents; //내용
    private String noticeType; //공지타입
    private Integer noticeHits; //조회수




}
