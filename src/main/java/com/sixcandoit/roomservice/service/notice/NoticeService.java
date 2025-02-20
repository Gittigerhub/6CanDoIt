package com.sixcandoit.roomservice.service.notice;


import com.sixcandoit.roomservice.dto.ImageFileDTO;
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
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;
    private final ImageFileService imageFileService;



    public void noticeRegister(NoticeDTO noticeDTO, List<MultipartFile> imageFiles) {

        try{
            // DTO => Entity 변환
            NoticeEntity notice=
                    modelMapper.map(noticeDTO, NoticeEntity.class);

            //이미지 등록
            log.info("이미지를 저장한다.");
            List<ImageFileEntity> images = imageFileService.saveImages(imageFiles);

            //이미지 정보 추가
            for(ImageFileEntity image : images){
                notice.addImage(image);
            }

            // DB에 저장
            log.info("저장을 수행한다");
            noticeRepository.save(notice);

//            //noticeImg 업데이트(첫 번째 이미지를 대표 이미지로 설정
//            noticeEntity.setNoticeImgFromImageFile();
//
//            //저장된 NoticeEntity에서 이미지 Url을 DTO로 전달
//            noticeDTO.setNoticeImg(noticeEntity.getNoticeImg());
        }catch (Exception e){
            throw new RuntimeException("이미지 저장 실패:" +e.getMessage());
        }
    }

    //수정
    public void noticeUpdate( NoticeDTO noticeDTO,String join,List<MultipartFile> imageFiles) {
        try {

            Optional<NoticeEntity> noticeEntitys = noticeRepository.findById(noticeDTO.getIdx());

            if (!noticeEntitys.isPresent()) {
                throw new RuntimeException("수정글 게시글 조회 실패");

            } else {
                NoticeEntity notice = modelMapper.map(noticeDTO, NoticeEntity.class);
                System.out.println(notice.toString());

                log.info("이미지를 저장한다");
                List<ImageFileEntity> images = imageFileService.updateImage(imageFiles, join, noticeDTO.getIdx());

                //이미지 정보 추가
                for (ImageFileEntity image : images) {
                    notice.addImage(image);

                }
                log.info("NoticeEntity 수정 진행");
                noticeRepository.save(notice);
                log.info("NoticeEntity 수정완료");
            }
        } catch (Exception e) {
            throw new RuntimeException("수정 오류 발생");
        }
    }

    public void noticeDelete(Integer idx,String join){
        try {
            // 이미지 조회
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(idx,join);

            // dto = > entity 변환
            List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                    .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                    .collect(Collectors.toList());

            // DB, 저장소에서 모든 이미지 삭제
            for(ImageFileEntity imageFileEntity : imageFileEntities){
                imageFileService.deleteImage(imageFileEntity.getIdx());
            }

            // idx로 공지사항 조회하여 삭제
            noticeRepository.deleteById(idx);

        }catch (Exception e) {
            throw new RuntimeException("삭제를 실패했습니다." + e.getMessage());
        }

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