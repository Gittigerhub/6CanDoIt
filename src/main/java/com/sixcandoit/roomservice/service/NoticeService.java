package com.sixcandoit.roomservice.service;

import com.sixcandoit.roomservice.dto.NoticeDTO;

import java.util.List;

public interface NoticeService {

    //글 등록
    public NoticeDTO register(NoticeDTO noticeDTO, String email);


    //글목록
    public List<NoticeDTO> list();

    //글 상세보기
    public NoticeDTO read(Long num);

    //글 수정
    public NoticeDTO update(NoticeDTO noticeDTO);






  






}
