package com.sixcandoit.roomservice.service.qna;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import com.sixcandoit.roomservice.entity.qna.ReplyEntity;
import com.sixcandoit.roomservice.repository.qna.QnaRepository;
import com.sixcandoit.roomservice.repository.qna.ReplyRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class QnaService {

    // final 선언, 모델 맵퍼 선언
    private final QnaRepository qnaRepository;
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;
    private final ImageFileService imageFileService;

    // Qna의 Q 쓰기
    public void qnaRegister(QnaDTO qnaDTO, List<MultipartFile> imageFiles) {

        System.out.println(imageFiles);
        try {
            // DTO를 Entity로 변환
            QnaEntity qna =
                    modelMapper.map(qnaDTO, QnaEntity.class);

            // FavYn의 기본값 N(1:1 질문)으로 설정
            log.info("favYn의 기본값을 N으로 설정...");
            qna.setFavYn("N");
            System.out.println(imageFiles);
            // 이미지 등록
            log.info("이미지를 저장한다...");
            List<ImageFileEntity> images = imageFileService.saveImages(imageFiles);
            System.out.println(imageFiles);
            // 이미지 정보 추가
            for (ImageFileEntity image : images) {
                qna.addImage(image);
            }
            System.out.println(imageFiles);
            // 저장
            log.info("저장을 수행한다...");
            qnaRepository.save(qna);

            // qnaImg 업데이트 (첫 번째 이미지를 대표 이미지로 설정)
            //qna.setQnaImgFromImageFile();

            // 저장된 QnaEntity에서 이미지 URL을 DTO로 전달
            //qnaDTO.setQnaImg(qna.getQnaImg());

        } catch (Exception e){
            throw new RuntimeException("이미지 저장 실패 : "+e.getMessage());
        }
    }

    // Qna의 Q 수정
    public void qnaUpdate(QnaDTO qnaDTO, String join, List<MultipartFile> imageFiles) {
        try {
            // 데이터의 idx를 조회
            Optional<QnaEntity> qnaEntitys = qnaRepository.findById(qnaDTO.getIdx());

            if (!qnaEntitys.isPresent()) { // QnaEntity가 존재하지 않으면
                throw new RuntimeException("수정할 게시글 조회 실패");
            } else {
                QnaEntity qna = modelMapper.map(qnaDTO, QnaEntity.class);
                System.out.println(qna.toString());

                //해당 QnaEntity에 관련된 답변이 존재하는지 확인
                log.info("해당 QnaEntity에 관련된 답변이 존재하는지 확인...");
                Optional<ReplyEntity> replyEntity = replyRepository.findByQnaJoin(qna);

                if (replyEntity.isPresent()){ // 답변이 존재하면 수정 불가
                    log.info("답변이 존재하면 수정 불가...");
                    throw new IllegalStateException("답변이 완료된 문의사항은 수정이 불가합니다.");
                }

//                // 이미지가 있을 때만 이미지 수정 로직 실행
//                if (qnaDTO.getFiles() != null && !qnaDTO.getFiles().isEmpty()) {
                    // 답변이 없으면 이미지 등록 (기존 이미지와 새 이미지 업데이트)
                    log.info("이미지를 저장한다...");
                    List<ImageFileEntity> images = imageFileService.updateImage(imageFiles, join, qnaDTO.getIdx());

                    // 이미지 정보 추가
                    for (ImageFileEntity image : images) {
//                        qnaEntity.deleteImage(image);
                        qna.addImage(image);  // 기존 이미지 삭제 후 새 이미지 추가
//                        qnaEntity.setQnaImg(images.getLast().getUrl());
                    }
                //}

                log.info("자주 묻는 질문 설정");
                // 자주 묻는 질문 설정
                if (qnaDTO.getFavYn() == null) { // FavYn이 null인 경우 기본값 "N" 설정
                    qnaDTO.setFavYn("N");
                }

                log.info("QnaEntity 수정 진행...");
                qnaRepository.save(qna);
                log.info("QnaEntity 수정 완료...");
            }
        }catch (Exception e){
            throw new RuntimeException("수정 오류 발생");
        }
    }

    // Qna의 Q 삭제
    public void qnaDelete(Integer idx, String join){
        try {
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(idx, join);List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                    .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                    .collect(Collectors.toList());

            // 모든 이미지 삭제
            for (ImageFileEntity imageFileEntity : imageFileEntities) {
                imageFileService.deleteImage(imageFileEntity.getIdx());
            }

            // idx로 조회하여 삭제
            qnaRepository.deleteById(idx);

        } catch (Exception e) {
            throw new RuntimeException("삭제를 실패했습니다." + e.getMessage());
        }
    }

    // Qna의 Q 의 전체목록, 데이터를 화면에 출력
    // 페이지 번호를 받아서 테이블의 해당 페이지의 데이터를 읽어와서 컨트롤러에 전달
    public Page<QnaDTO> qnaList(Pageable page, String type, String keyword){

        int currentPage = page.getPageNumber()-1; // 화면의 페이지 번호를 db 페이지 번호로
        int pageLimit = 10; // 한 페이지를 구성하는 레코드 수

        // 지정된 내용으로 페이지 정보를 재생산, 정렬 내림차순 DESC
        Pageable pageable = PageRequest.of(currentPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "idx"));

        // 조회한 변수를 선언
        // type : 제목(1), 내용(2), 제목+내용(2), 답변만(4), 자주 묻는 질문(5), 전체(0)
        Page<QnaEntity> qnaEntities;
        if (keyword != null){ // 검색어가 존재하면
            log.info("검색어가 존재하면...");
            if (type.equals("1")){ // type 분류 1, 제목으로 검색할 때
                log.info("제목으로 검색을 하는 중...");
                qnaEntities = qnaRepository.searchQnaTitle(keyword, pageable);
            } else if (type.equals("2")){ // type 분류 2, 내용으로 검색할 때
                log.info("내용으로 검색을 하는 중...");
                qnaEntities = qnaRepository.searchQnaContents(keyword, pageable);
            } else if (type.equals("3")){ // type 분류 3, 제목+내용으로 검색할 때
                log.info("제목+내용으로 검색을 하는 중...");
                qnaEntities = qnaRepository.searchQnaAll(keyword, pageable);
            } else if (type.equals("4")){ // type 분류 4, 답변만 검색할 때
                log.info("답변만 검색하는 중...");
                qnaEntities = qnaRepository.searchReplyAll(keyword, pageable);
            } else if (type.equals("5")) { // type 분류 5, 자주 묻는 질문만 검색할 때
                log.info("자주 묻는 질문만 검색 중...");
                qnaEntities = qnaRepository.searchFavYn(pageable);
            } else { // 전체 검색 = 0
                log.info("전체 조회 검색 중...");
                qnaEntities = qnaRepository.searchQnaAndReply(keyword, pageable);
            }
        } else { // 검색어가 존재하지 않으면 모두 검색
            qnaEntities = qnaRepository.findAll(pageable);
        }

        // Entity를 DTO로 변환 후 저장
        Page<QnaDTO> qnaDTOS = qnaEntities.map(
                data->modelMapper.map(data, QnaDTO.class));

        return qnaDTOS;
    }

    // 아니면 조회수 증가를 만들어따로
    public void count(Integer idx){
        QnaEntity qnaEntity = qnaRepository.findById(idx)
                        .orElseThrow();

        qnaEntity.setQnaHits(qnaEntity.getQnaHits()+1);

        qnaRepository.save(qnaEntity);
    }
    // Qna의 Q의 개별정보, 게시글 번호의 데이터를 화면에 출력
    public QnaDTO qnaRead(Integer idx){
        Optional<QnaEntity> qnaEntity = qnaRepository.findById(idx);

        QnaDTO qnaDTO = modelMapper.map(qnaEntity, QnaDTO.class);

        return qnaDTO;
    }

    // 자주 묻는 질문 설정 (favYn) 업데이트
    public void updateFavYn(Integer idx, String favYn) {
        // Qna 엔티티 조회
        Optional<QnaEntity> qnaEntityOpt = qnaRepository.findById(idx);

        if (qnaEntityOpt.isPresent()) {
            // QnaEntity 가져오기
            QnaEntity qnaEntity = qnaEntityOpt.get();

            // favYn 값 업데이트
            qnaEntity.setFavYn(favYn);

            // 변경된 값을 DB에 저장
            qnaRepository.save(qnaEntity);

            log.info("Qna(idx=" + idx + ")의 favYn이 " + favYn + "으로 업데이트되었습니다.");
        } else {
            // 해당 Qna가 존재하지 않으면 예외 처리
            throw new IllegalStateException("해당 Qna가 존재하지 않습니다.");
        }
    }
}