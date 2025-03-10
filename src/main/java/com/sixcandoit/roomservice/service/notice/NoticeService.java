package com.sixcandoit.roomservice.service.notice;


import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.notice.NoticeDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.notice.NoticeEntity;
import com.sixcandoit.roomservice.repository.notice.NoticeRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
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
    public void noticeUpdate( NoticeDTO noticeDTO, String join, List<MultipartFile> imageFiles) {
        try {
            // 기존의 공지사항 조회
            Optional<NoticeEntity> noticeEntity = noticeRepository.findById(noticeDTO.getIdx());

            if (noticeEntity.isEmpty()) { // QnaEntity가 존재하지 않으면
                throw new RuntimeException("수정할 게시글 조회 실패");

            } else {
                // 받은 DTO를 Entity로 변환
                NoticeEntity notice = modelMapper.map(noticeDTO, NoticeEntity.class);

                // 빈 파일 제거 후 유효한 파일 리스트만 남김
                // 자꾸 빈파일이 넘겨져 파일을 넘길때와 같이 길이가 1이되어 오류가 생김으로 유효리스트 생성
                List<MultipartFile> validImageFiles = imageFiles.stream()
                        .filter(file -> file != null && !file.isEmpty()) // 비어 있지 않은 파일만 필터링
                        .collect(Collectors.toList());

                System.out.println("유효한 이미지 파일 개수: " + validImageFiles.size());

                // 이미지 들어오면
                if (!validImageFiles.isEmpty()) {
                    System.out.println("이미지 작업 시작!!!!");
                    // 기존 이미지 조회
                    List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(noticeDTO.getIdx(), join);

                    // 기존 이미지 삭제
                    // dto = > entity 변환
                    log.info("이미지를 삭제한다");
                    List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                            .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                            .collect(Collectors.toList());

                    // DB, 저장소에서 모든 이미지 삭제
                    for(ImageFileEntity imageFileEntity : imageFileEntities){
                        imageFileService.deleteImage(imageFileEntity.getIdx());
                    }

                    // 새로운 이미지 등록
                    log.info("이미지를 저장한다");
                    List<ImageFileEntity> images = imageFileService.saveImages(imageFiles);

                    // 자동 FK 생성
                    for (ImageFileEntity image : images) {
                        notice.addImage(image);

                    }

                } else {
                    // modelmapper를 이용하면 새로운 객체가 되어 기존 이미지 연관관계가 사라져 버리기 때문에
                    // 새로 설정해야함
                    System.out.println("이미지 작업2 시작!!!!");
                    // 기존 이미지 조회
                    List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(noticeDTO.getIdx(), join);

                    // dto = > entity 변환
                    List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                            .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                            .collect(Collectors.toList());

                    // 자동 FK 생성
                    for (ImageFileEntity image : imageFileEntities) {
                        notice.addImage(image);

                    }

                }

                log.info("NoticeEntity 수정 진행");
                noticeRepository.save(notice);
                log.info("NoticeEntity 수정완료");

            }

        } catch (Exception e) {
            throw new RuntimeException("수정 오류 발생");
        }

    }

    public void noticeDelete(Integer idx, String join) {
        try {
            // 이미지 삭제
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(idx, join);
            
            // 이미지가 있는 경우에만 삭제 처리
            if (imageFileDTOS != null && !imageFileDTOS.isEmpty()) {
                log.info("이미지 삭제 시작: {} 개의 이미지", imageFileDTOS.size());
                List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                        .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                        .collect(Collectors.toList());

                // DB와 저장소에서 이미지 삭제
                for (ImageFileEntity imageFileEntity : imageFileEntities) {
                    try {
                        imageFileService.deleteImage(imageFileEntity.getIdx());
                    } catch (Exception e) {
                        log.error("이미지 삭제 중 오류 발생 (idx: {}): {}", imageFileEntity.getIdx(), e.getMessage());
                        // 이미지 삭제 실패해도 계속 진행
                    }
                }
            } else {
                log.info("삭제할 이미지가 없습니다. idx: {}", idx);
            }

            // 공지사항 삭제
            noticeRepository.deleteById(idx);
            log.info("공지사항 삭제 완료: {}", idx);
        } catch (Exception e) {
            log.error("공지사항 삭제 실패 (idx: {}): {}", idx, e.getMessage());
            throw new RuntimeException("공지사항 삭제 실패: " + e.getMessage());
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