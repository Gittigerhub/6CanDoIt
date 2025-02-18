package com.sixcandoit.roomservice.service.notice;


import com.sixcandoit.roomservice.dto.notice.NoticeDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.notice.NoticeEntity;
import com.sixcandoit.roomservice.repository.notice.NoticeRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private ModelMapper modelMapper=new ModelMapper();
    private final ImageFileService imageFileService;



    public void noticeRegister( NoticeDTO noticeDTO) {

        try{
            NoticeEntity noticeEntity=
                    modelMapper.map(noticeDTO, NoticeEntity.class);

            //이미지 등록
            log.info("이미지를 저장한다.");
            List<ImageFileEntity> images= imageFileService.saveImages(noticeDTO.getFiles());

            //이미지 정보 추가
            for(ImageFileEntity image:images){
                noticeEntity.addImage(image);
            }
            log.info("저장을 수행한다");
            noticeRepository.save(noticeEntity);

            //noticeImg 업데이트(첫 번째 이미지를 대표 이미지로 설정
            noticeEntity.setNoticeImgFromImageFile();

            //저장된 NoticeEntity에서 이미지 Url을 DTO로 전달
            noticeDTO.setNoticeImg(noticeEntity.getNoticeImg());
        }catch (Exception e){
            throw new RuntimeException("이미지 저장 실패:" +e.getMessage());
        }
    }


    public void noticeUpdate( NoticeDTO noticeDTO)  throws  Exception {
        Optional<NoticeEntity> noticeEntityOpt= noticeRepository.findById(noticeDTO.getIdx());

        if(noticeEntityOpt.isPresent()){
            NoticeEntity noticeEntity=noticeEntityOpt.get();

            log.info("이미지를 저장한다");
            List<ImageFileEntity> images=imageFileService.updateImage(noticeDTO.getFiles(),"notice",noticeDTO.getIdx());

            //기존 이미지 업데이트
            noticeEntity.updateImages(images);

            log.info("대표 이미지 설정");
            if(noticeEntity.getNoticeImg()==null){
                if(!images.isEmpty()){
                    noticeEntity.setNoticeImg(images.get(0).getUrl());
                }
            }
        }
    }

    public void noticeDelete(Integer idx) {noticeRepository.deleteById(idx);}


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

