package com.sixcandoit.roomservice.service.qna;

import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import com.sixcandoit.roomservice.entity.qna.ReplyEntity;
import com.sixcandoit.roomservice.repository.qna.QnaRepository;
import com.sixcandoit.roomservice.repository.qna.ReplyRepository;
import com.sixcandoit.roomservice.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class QnaService {
    @Value("${imgUploadLocation}") //이미지가 저장될 위치
    private String imgUploadLocation;

    // final 선언, 모델 맵퍼 선언
    private final QnaRepository qnaRepository;
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;
    private final S3Uploader s3Uploader;

    // Qna의 Q 쓰기
    public void qnaRegister(QnaDTO qnaDTO, MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename(); //파일이름분리
        String newFileName = ""; //업로드 성공시 생성된 파일이름

        if (originalFileName != null) { //작업할 파일이 존재하면
            newFileName = s3Uploader.upload(file, imgUploadLocation); //S3 업로드
        }
        qnaDTO.setQnaImg(newFileName); //이미지파일에 새이름을저장

        // DTO를 Entity로 변환
        QnaEntity qnaEntity =
                modelMapper.map(qnaDTO, QnaEntity.class);
        // FavYn의 기본값 N(1:1 질문)으로 설정
        qnaEntity.setFavYn("N");

        // 저장
        qnaRepository.save(qnaEntity);
    }

    // Qna의 Q 수정
    public void qnaUpdate(QnaDTO qnaDTO, MultipartFile file) throws IOException {
        // 데이터의 idx를 조회
        Optional<QnaEntity> qnaEntity = qnaRepository.findById(qnaDTO.getIdx());
        String deleteFile = qnaDTO.getQnaImg(); //이전 이미지파일 읽기
        String originalFileName = file.getOriginalFilename(); //저장할 파일명
        String newFileName = ""; //저장후 생성된 새로운 파일명

        if (qnaEntity.isPresent()){ // QnaEntity가 존재하면
            //해당 QnaEntity에 관련된 답변이 존재하는지 확인
            log.info("해당 QnaEntity에 관련된 답변이 존재하는지 확인...");
            Optional<ReplyEntity> replyEntity = replyRepository.findByQnaJoin(qnaEntity.get());

            if (replyEntity.isPresent()){ // 답변이 존재하면 수정 불가
                log.info("답변이 존재하면 수정 불가...");
                throw new IllegalStateException("답변이 완료된 문의사항은 수정이 불가합니다.");
            }
            if (originalFileName.length() != 0){ //작업할 파일이 존재하면
                if (deleteFile.length() != 0){ //기존파일이 존재하면
                    s3Uploader.deleteFile(deleteFile, imgUploadLocation); //기존파일 삭제
                }
                newFileName = s3Uploader.upload(file, imgUploadLocation); //새로운 파일을 S3에 업로드
                qnaDTO.setQnaImg(newFileName); //업로드한 새로운 파일명으로 변경
            }
            // 답변이 없으면 QnaEntity 수정 진행
            log.info("답변이 없으면 QnaEntity 수정 진행...");
            QnaEntity qnaEntitys = modelMapper.map(qnaDTO, QnaEntity.class);
            qnaRepository.save(qnaEntitys);
        } else {
            throw new IllegalStateException("수정할 QnA가 존재하지 않습니다.");
        }
    }

    // Qna의 Q 삭제
    public void qnaDelete(Integer idx){
        qnaRepository.deleteById(idx);
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

