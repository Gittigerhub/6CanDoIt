package com.sixcandoit.roomservice.service.notice;

import com.sixcandoit.roomservice.dto.notice.NoticeDTO;
import com.sixcandoit.roomservice.entity.notice.NoticeEntity;
import com.sixcandoit.roomservice.repository.notice.NoticeRepository;
import com.sixcandoit.roomservice.service.S3Uploader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
@Log
@RequiredArgsConstructor // Lombok을 이용한 생성자 자동 생성
public class NoticeService {
    @Value("images")
    private String imgUploadLocation;

    private final NoticeRepository noticeRepository; // final로 설정 (반드시 주입되어야 함)
    private final ModelMapper modelMapper;
    private final S3Uploader s3Uploader;

    public void noticeRegister(NoticeDTO noticeDTO, MultipartFile  file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String newFileName ="";

        if(originalFilename !=null){
            newFileName = s3Uploader.upload(file,imgUploadLocation);
        }
        noticeDTO.setNoticeImg(newFileName);


        NoticeEntity noticeEntity =
                modelMapper.map(noticeDTO, NoticeEntity.class);
        //저장
        noticeRepository.save(noticeEntity);
    }

    public void noticeUpdate(NoticeDTO noticeDTO,MultipartFile  file) throws IOException {

        //데이터의 idx를 조회
        Optional<NoticeEntity> noticeEntity = noticeRepository.findById(noticeDTO.getIdx());
        String deleteFile = noticeDTO.getNoticeImg();
        String originalFilename = file.getOriginalFilename();
        String newFileName ="";

        if (noticeEntity.isPresent()) {
            NoticeEntity noticeEntity1 = modelMapper.map(noticeDTO, NoticeEntity.class);
            noticeRepository.save(noticeEntity1);
        }
        if(originalFilename.length()!=0){
            if(deleteFile.length()!=-0){
                s3Uploader.deleteFile(deleteFile,imgUploadLocation);
            }
            newFileName = s3Uploader.upload(file,imgUploadLocation);
            noticeDTO.setNoticeTitle(newFileName);
        }
    }

    public void noticeDelete(Integer idx) {noticeRepository.deleteById(idx);
    }

    public Page<NoticeDTO> noticeList(Pageable page, String type, String keyword) {
        Pageable pageable = PageRequest.of(Math.max(page.getPageNumber() - 1, 0), 10);

        Page<NoticeEntity> noticeEntities;
        if (keyword != null) {
            log.info("검색어가 존재하면");
            if (type.equals("1")) {
                log.info("제목으로 검색을 하는 중");
                noticeEntities = noticeRepository.searchNoticeTitle(keyword, pageable);
            } else if (type.equals("2")) {
                log.info("내용으로 검색하는 중");
                noticeEntities = noticeRepository.searchNoticeContents(keyword, pageable);
            } else {
                log.info("모든 대상으로 검색을 하는 중");
                noticeEntities = noticeRepository.searchAll(keyword, pageable);
            }
        } else {
            noticeEntities = noticeRepository.findAll(pageable);
        }
        Page<NoticeDTO> noticeDTOS =noticeEntities.map(
                data -> modelMapper.map(data, NoticeDTO.class));


        return noticeDTOS;
    }

    public void count(Integer idx) {
        NoticeEntity noticeEntity = noticeRepository.findById(idx).orElseThrow();

        noticeEntity.setNoticeHits(noticeEntity.getNoticeHits() + 1);

        noticeRepository.save(noticeEntity);
    }

    public NoticeDTO noticeRead(Integer idx) {
        // 1. 게시글 조회
        Optional<NoticeEntity> noticeEntity = noticeRepository.findById(idx);

        NoticeDTO noticeDTO = modelMapper.map(noticeEntity, NoticeDTO.class);

        return noticeDTO;

    }
}
