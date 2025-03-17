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
            NoticeEntity notice = modelMapper.map(noticeDTO, NoticeEntity.class);

            List<ImageFileEntity> images = imageFileService.saveImages(imageFiles);
            images.forEach(notice::addImage);

            noticeRepository.save(notice);
            log.info("공지사항 등록 완료: {}", notice);
        } catch (Exception e) {
            log.error("공지사항 등록 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("공지사항 등록 실패: " + e.getMessage());
        }
    }

    // 공지사항 수정
    public void noticeUpdate(NoticeDTO noticeDTO, String join, List<MultipartFile> imageFiles) {
        try {
            NoticeEntity notice = noticeRepository.findById(noticeDTO.getIdx())
                    .orElseThrow(() -> new RuntimeException("수정할 공지사항이 존재하지 않습니다."));

            List<MultipartFile> validImageFiles = imageFiles.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .collect(Collectors.toList());

            if (!validImageFiles.isEmpty()) {
                deleteExistingImages(imageFileService.readImage(noticeDTO.getIdx(), join));
                List<ImageFileEntity> images = imageFileService.saveImages(validImageFiles);
                images.forEach(notice::addImage);
            }

            noticeRepository.save(notice);
            log.info("공지사항 수정 완료: {}", notice);
        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("공지사항 수정 실패: " + e.getMessage());
        }
    }

    // 공지사항 삭제
    public void deleteNotice(Integer idx, String join) {
        try {
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(idx, join);
            deleteExistingImages(imageFileDTOS);

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

    // 공지사항 목록 조회
    public Page<NoticeDTO> noticeList(Pageable page, String type, String keyword) {
        Pageable pageable = PageRequest.of(Math.max(page.getPageNumber() - 1, 0), 10);
        Page<NoticeEntity> noticeEntities = keyword != null ?
                searchNoticesByKeyword(type, keyword, pageable) :
                noticeRepository.findAll(pageable);

        return noticeEntities.isEmpty() ? Page.empty(pageable) :
                noticeEntities.map(data -> modelMapper.map(data, NoticeDTO.class));
    }

    // 검색 조건에 따른 공지사항 검색
    private Page<NoticeEntity> searchNoticesByKeyword(String type, String keyword, Pageable pageable) {
        return switch (type) {
            case "1" -> noticeRepository.searchNoticeTitle(keyword, pageable);
            case "2" -> noticeRepository.searchNoticeContents(keyword, pageable);
            default -> noticeRepository.searchAll(keyword, pageable);
        };
    }

    // 조회수 증가
    public void count(Integer idx) {
        NoticeEntity noticeEntity = noticeRepository.findById(idx)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        noticeEntity.setNoticeHits(noticeEntity.getNoticeHits() + 1);
        noticeRepository.save(noticeEntity);
    }

    // 공지사항 읽기
    public NoticeDTO noticeRead(Integer idx) {
        return noticeRepository.findById(idx)
                .map(notice -> modelMapper.map(notice, NoticeDTO.class))
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
    }

    // 타입에 따른 공지사항 목록 조회
    public Page<NoticeDTO> getNoticeListByType(Pageable page, String type, String keyword, String noticeType) {
        Pageable pageable = PageRequest.of(Math.max(page.getPageNumber() - 1, 0), 10);
        Page<NoticeEntity> noticeEntities = (keyword == null || keyword.isEmpty()) ?
                noticeRepository.findAllByNoticeType(noticeType, pageable) :
                searchNoticesByTypeAndKeyword(type, keyword, noticeType, pageable);

        return noticeEntities.isEmpty() ? Page.empty(pageable) :
                noticeEntities.map(notice -> modelMapper.map(notice, NoticeDTO.class));
    }

    // 타입과 키워드에 따른 공지사항 검색
    private Page<NoticeEntity> searchNoticesByTypeAndKeyword(String type, String keyword, String noticeType, Pageable pageable) {
        return switch (type) {
            case "title" -> noticeRepository.findByNoticeTitleContainingAndNoticeType(keyword, noticeType, pageable);
            case "content" -> noticeRepository.findByNoticeContentsContainingAndNoticeType(keyword, noticeType, pageable);
            default -> noticeRepository.findAllByNoticeTypeAndTitleOrContentsContaining(noticeType, keyword, pageable);
        };
    }

    public void deleteNotice(Integer idx) {
    }
}
