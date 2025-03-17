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

    // 공지사항 등록
    public void noticeRegister(NoticeDTO noticeDTO, List<MultipartFile> imageFiles) {
        try {
            // DTO => Entity 변환
            NoticeEntity notice = modelMapper.map(noticeDTO, NoticeEntity.class);

            // 이미지 등록
            log.info("이미지를 저장한다.");
            List<ImageFileEntity> images = imageFileService.saveImages(imageFiles);

            // 이미지 정보 추가
            for (ImageFileEntity image : images) {
                notice.addImage(image);
            }

            // DB에 저장
            log.info("저장을 수행한다");
            noticeRepository.save(notice);
        } catch (Exception e) {
            log.error("공지사항 등록 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("공지사항 등록 실패: " + e.getMessage());
        }
    }

    // 공지사항 수정
    public void noticeUpdate(NoticeDTO noticeDTO, String join, List<MultipartFile> imageFiles) {
        try {
            // 기존의 공지사항 조회
            Optional<NoticeEntity> noticeEntity = noticeRepository.findById(noticeDTO.getIdx());

            if (noticeEntity.isEmpty()) {
                throw new RuntimeException("수정할 게시글 조회 실패");
            } else {
                // 받은 DTO를 Entity로 변환
                NoticeEntity notice = modelMapper.map(noticeDTO, NoticeEntity.class);

                // 유효한 이미지 파일 리스트만 남기기
                List<MultipartFile> validImageFiles = imageFiles.stream()
                        .filter(file -> file != null && !file.isEmpty()) // 비어 있지 않은 파일만 필터링
                        .collect(Collectors.toList());

                log.info("유효한 이미지 파일 개수: {}", validImageFiles.size());

                // 새로운 이미지가 있으면
                if (!validImageFiles.isEmpty()) {
                    log.info("이미지 작업 시작!!!!");

                    // 기존 이미지 삭제
                    List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(noticeDTO.getIdx(), join);
                    log.info("기존 이미지 삭제");
                    deleteExistingImages(imageFileDTOS);

                    // 새로운 이미지 등록
                    log.info("새로운 이미지를 저장한다");
                    List<ImageFileEntity> images = imageFileService.saveImages(validImageFiles);

                    // 새로운 이미지 추가
                    for (ImageFileEntity image : images) {
                        notice.addImage(image);
                    }
                } else {
                    log.info("기존 이미지 유지");
                    // 기존 이미지를 유지
                    List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(noticeDTO.getIdx(), join);
                    addExistingImagesToNotice(imageFileDTOS, notice);
                }

                // 수정된 공지사항 저장
                log.info("공지사항 수정 진행");
                noticeRepository.save(notice);
                log.info("공지사항 수정 완료");
            }

        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("공지사항 수정 실패: " + e.getMessage());
        }
    }

    // 공지사항 삭제
    public void noticeDelete(Integer idx, String join) {
        try {
            // 이미지 삭제
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(idx, join);

            // 이미지가 있으면 삭제
            if (imageFileDTOS != null && !imageFileDTOS.isEmpty()) {
                log.info("이미지 삭제 시작: {} 개의 이미지", imageFileDTOS.size());
                deleteExistingImages(imageFileDTOS);
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

    // 기존 이미지 삭제
    private void deleteExistingImages(List<ImageFileDTO> imageFileDTOS) {
        List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                .collect(Collectors.toList());

        for (ImageFileEntity imageFileEntity : imageFileEntities) {
            try {
                imageFileService.deleteImage(imageFileEntity.getIdx());
            } catch (Exception e) {
                log.error("이미지 삭제 중 오류 발생 (idx: {}): {}", imageFileEntity.getIdx(), e.getMessage());
            }
        }
    }

    // 기존 이미지를 공지사항에 추가
    private void addExistingImagesToNotice(List<ImageFileDTO> imageFileDTOS, NoticeEntity notice) {
        List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                .collect(Collectors.toList());

        for (ImageFileEntity image : imageFileEntities) {
            notice.addImage(image);
        }
    }

    // 공지사항 목록 조회
    public Page<NoticeDTO> noticeList(Pageable page, String type, String keyword) {
        Pageable pageable = PageRequest.of(Math.max(page.getPageNumber() - 1, 0), 10);
        Page<NoticeEntity> noticeEntities;

        if (keyword != null) {
            log.info("검색어가 존재하면");
            noticeEntities = searchNoticesByKeyword(type, keyword, pageable);
        } else {
            noticeEntities = noticeRepository.findAll(pageable);
        }

        return noticeEntities.map(data -> modelMapper.map(data, NoticeDTO.class));
    }

    // 검색 조건에 따른 공지사항 검색
    private Page<NoticeEntity> searchNoticesByKeyword(String type, String keyword, Pageable pageable) {
        Page<NoticeEntity> noticeEntities;
        if (type.equals("1")) {
            log.info("제목으로 검색");
            noticeEntities = noticeRepository.searchNoticeTitle(keyword, pageable);
        } else if (type.equals("2")) {
            log.info("내용으로 검색");
            noticeEntities = noticeRepository.searchNoticeContents(keyword, pageable);
        } else {
            log.info("모든 대상으로 검색");
            noticeEntities = noticeRepository.searchAll(keyword, pageable);
        }
        return noticeEntities;
    }

    // 조회수 증가
    public void count(Integer idx) {
        NoticeEntity noticeEntity = noticeRepository.findById(idx).orElseThrow();

        noticeEntity.setNoticeHits(noticeEntity.getNoticeHits() + 1);

        noticeRepository.save(noticeEntity);
    }

    // 공지사항 읽기
    public NoticeDTO noticeRead(Integer idx) {
        Optional<NoticeEntity> noticeEntity = noticeRepository.findById(idx);

        return modelMapper.map(noticeEntity.orElseThrow(), NoticeDTO.class);
    }

    // 타입에 따른 공지사항 목록 조회
    public Page<NoticeDTO> getNoticeListByType(Pageable page, String type, String keyword, String noticeType) {
        Pageable pageable = PageRequest.of(Math.max(page.getPageNumber() - 1, 0), 10);
        Page<NoticeEntity> noticeEntities;

        if (keyword == null || keyword.isEmpty()) {
            noticeEntities = noticeRepository.findAllByNoticeType(noticeType, pageable);
        } else {
            noticeEntities = searchNoticesByTypeAndKeyword(type, keyword, noticeType, pageable);
        }

        return noticeEntities.map(notice -> modelMapper.map(notice, NoticeDTO.class));
    }

    // 타입과 키워드에 따른 공지사항 검색
    private Page<NoticeEntity> searchNoticesByTypeAndKeyword(String type, String keyword, String noticeType, Pageable pageable) {
        Page<NoticeEntity> noticeEntities;
        if (type.equals("title")) {
            noticeEntities = noticeRepository.findByNoticeTitleContainingAndNoticeType(keyword, noticeType, pageable);
        } else if (type.equals("content")) {
            noticeEntities = noticeRepository.findByNoticeContentsContainingAndNoticeType(keyword, noticeType, pageable);
        } else {
            noticeEntities = noticeRepository.findAllByNoticeTypeAndTitleOrContentsContaining(noticeType, keyword, pageable);
        }
        return noticeEntities;
    }

    public void deleteNotice(Integer idx) {

    }
}
