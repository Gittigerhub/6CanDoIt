package com.sixcandoit.roomservice.service.qna;

import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import com.sixcandoit.roomservice.repository.qna.QnaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class QnaService {

    // final 선언, 모델 맵퍼 선언
    private final QnaRepository qnaRepository;
    private final ModelMapper modelMapper;

    // Qna의 Q 쓰기
    public void qnaRegister(QnaDTO qnaDTO){
        // DTO로 Entity 변환
        QnaEntity qnaEntity = modelMapper.map(qnaDTO, QnaEntity.class);
        // 저장
        qnaRepository.save(qnaEntity);
    }

    // Qna의 Q 수정
    public void qnaUpdate(QnaDTO qnaDTO){
        // 데이터의 id를 조회
        Optional<QnaEntity> qnaEntity = qnaRepository.findById(qnaDTO.getIdx());

        if (qnaEntity.isPresent()){ // 존재하면 수정
            QnaEntity qnaEntitys = modelMapper.map(qnaDTO, QnaEntity.class);
            qnaRepository.save(qnaEntitys);
        }
    }

    // Qna의 Q 삭제
    public void qnaDelete(Integer idx){
        qnaRepository.deleteById(idx);
    }

    // Qna의 Q 의 전체목록, 데이터를 화면에 출력
    // 페이지 번호를 받아서 테이블의 해당 페이지의 데이터를 읽어와서 컨트롤러에 전달
    public Page<QnaDTO> qnaList(Pageable page){

        int currentPage = page.getPageNumber()-1; // 화면의 페이지 번호를 db 페이지 번호로
        int pageLimit = 5; // 한 페이지를 구성하는 레코드 수

        // 지정된 내용으로 페이지 정보를 재생산
        // 정렬은 최신순, 내림차순 DESC
        Pageable pageable = PageRequest.of(currentPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "idx"));

        // 페이지 정보에 해당하는 모든 데이터 조회
        Page<QnaEntity> qnaEntities = qnaRepository.findAll(pageable);

        // Entity를 DTO로 변환 후 저장
        Page<QnaDTO> qnaDTOS = qnaEntities.map(
                data->modelMapper.map(data, QnaDTO.class));

        return qnaDTOS;
    }

    // Qna의 Q의 개별정보, 게시글 번호의 데이터를 화면에 출력
    public QnaDTO qnaRead(Integer idx){
        Optional<QnaEntity> qnaEntity = qnaRepository.findById(idx);
        log.info("게시글의 idx를 조회...");
        QnaDTO qnaDTO = modelMapper.map(qnaEntity, QnaDTO.class);
        log.info("DTO로 변환하는 중...");
        return qnaDTO;
    }
}

