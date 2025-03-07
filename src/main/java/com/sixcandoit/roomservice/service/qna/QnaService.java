package com.sixcandoit.roomservice.service.qna;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import com.sixcandoit.roomservice.entity.qna.ReplyEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.qna.QnaRepository;
import com.sixcandoit.roomservice.repository.qna.ReplyRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class QnaService {

    // final 선언, 모델 맵퍼 선언
    private final QnaRepository qnaRepository;
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;
    private final ImageFileService imageFileService;

    // Qna의 Q 쓰기
    public void qnaRegister(QnaDTO qnaDTO, List<MultipartFile> imageFiles) throws Exception {
        log.info("QnA 등록을 시작합니다.");

        // QnaDTO를 QnaEntity로 변환
        QnaEntity qna = modelMapper.map(qnaDTO, QnaEntity.class);

        // 기본값 설정
        qna.setFavYn("N");  // 자주 묻는 질문 기본값: N
        qna.setReplyYn("N"); // 답변 여부 기본값: N
        qna.setQnaHits(0);    // 조회수 기본값: 0

        // 작성자 정보 설정
        qna.setMemberName(qnaDTO.getMemberName());
        qna.setMemberJoin(modelMapper.map(qnaDTO.getMemberDTO(), MemberEntity.class));

        log.info("작성자 정보 설정 완료 - 이름: {}", qna.getMemberName());

        // QnA 저장
        QnaEntity savedQna = qnaRepository.save(qna);

        // 이미지 파일 처리
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<ImageFileEntity> images = imageFileService.saveImages(imageFiles);
            // 자동 FK 생성
            for (ImageFileEntity image : images) {
                savedQna.addImage(image);
            }
            qnaRepository.save(savedQna);
        }

        log.info("QnA 등록이 완료되었습니다.");
    }

    // Qna의 Q 수정
    public void qnaUpdate(QnaDTO qnaDTO, String join, List<MultipartFile> imageFiles) {
        try {
            System.out.println("이미지 파일즈 길이 : " + imageFiles.size());
            // 데이터의 idx를 조회
            Optional<QnaEntity> qnaEntityOptional = qnaRepository.findById(qnaDTO.getIdx());

            if (qnaEntityOptional.isEmpty()) { // QnaEntity가 존재하지 않으면
                throw new RuntimeException("수정할 게시글 조회 실패");
            }

            QnaEntity existingQna = qnaEntityOptional.get();
            
            // 작성자 본인 확인
            if (!existingQna.getMemberJoin().getIdx().equals(qnaDTO.getMemberDTO().getIdx())) {
                throw new RuntimeException("자신이 작성한 글만 수정할 수 있습니다.");
            }

            System.out.println("수정작업 시작!!!!!!!");
            // DTO => Entity
            QnaEntity qna = modelMapper.map(qnaDTO, QnaEntity.class);

            // 회원 정보 설정
            MemberEntity memberEntity = modelMapper.map(qnaDTO.getMemberDTO(), MemberEntity.class);
            qna.setMemberJoin(memberEntity);
            qna.setMemberName(qnaDTO.getMemberName());

            //해당 QnaEntity에 관련된 답변이 존재하는지 확인
            log.info("해당 QnaEntity에 관련된 답변이 존재하는지 확인...");
            Optional<ReplyEntity> replyEntity = replyRepository.findByQnaJoin(qna);

            if (replyEntity.isPresent()){ // 답변이 존재하면 수정 불가
                log.info("답변이 존재하면 수정 불가...");
                throw new IllegalStateException("답변이 완료된 문의사항은 수정이 불가합니다.");

            } else {
                System.out.println("통과!!!!!!!");

                // 빈 파일 제거 후 유효한 파일 리스트만 남김
                List<MultipartFile> validImageFiles = imageFiles.stream()
                        .filter(file -> file != null && !file.isEmpty()) // 비어 있지 않은 파일만 필터링
                        .collect(Collectors.toList());

                System.out.println("유효한 이미지 파일 개수: " + validImageFiles.size());

                // 이미지 들어오면
                if (!validImageFiles.isEmpty()) {
                    System.out.println("이미지 작업 시작!!!!");
                    // 기존 이미지 조회
                    List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(qnaDTO.getIdx(), join);

                    // 기존 이미지 삭제
                    log.info("이미지를 삭제한다");
                    List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                            .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                            .collect(Collectors.toList());

                    // DB, 저장소에서 모든 이미지 삭제
                    for (ImageFileEntity imageFileEntity : imageFileEntities) {
                        imageFileService.deleteImage(imageFileEntity.getIdx());
                    }

                    // 새로운 이미지 등록
                    log.info("이미지를 저장한다");
                    List<ImageFileEntity> images = imageFileService.saveImages(imageFiles);

                    // 자동 FK 생성
                    for (ImageFileEntity image : images) {
                        qna.addImage(image);
                    }

                } else {
                    // modelmapper를 이용하면 새로운 객체가 되어 기존 이미지 연관관계가 사라져 버리기 때문에
                    // 새로 설정해야함
                    System.out.println("이미지 작업2 시작!!!!");
                    // 기존 이미지 조회
                    List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(qnaDTO.getIdx(), join);

                    // dto = > entity 변환
                    List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                            .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                            .collect(Collectors.toList());

                    // 자동 FK 생성
                    for (ImageFileEntity image : imageFileEntities) {
                        qna.addImage(image);
                    }
                }

                log.info("자주 묻는 질문 설정");
                // 자주 묻는 질문 설정
                if (qnaDTO.getFavYn() == null) { // FavYn이 null인 경우 기본값 "N" 설정
                    qnaDTO.setFavYn("N");
                }

                log.info("QnaEntity 수정 진행...");
                qnaRepository.save(qna);
                log.info("QnaEntity 수정 완료...");
            }
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
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
            } else if (type.equals("6")) { // type 분류 6, 미답변만 검색할 때
                log.info("미답변만 검색 중...");
                qnaEntities = qnaRepository.searchUnreplied(pageable);
            } else if (type.equals("7")) { // type 분류 7, 답변완료만 검색할 때
                log.info("답변완료만 검색 중...");
                qnaEntities = qnaRepository.searchReplied(pageable);
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