package com.sixcandoit.roomservice.service.qna;

import com.sixcandoit.roomservice.dto.qna.QnaDTO;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import com.sixcandoit.roomservice.repository.qna.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class QnaService {

    // final 선언, 모델 맵퍼 선언
    private final QnaRepository qnaRepository;
    private final ModelMapper modelMapper;

    // Qna의 Q 쓰기
    public void register(QnaDTO qnaDTO){
        // DTO로 Entity 변환
        QnaEntity qnaEntity = modelMapper.map(qnaDTO, QnaEntity.class);
        // 저장
        qnaRepository.save(qnaEntity);
    }

    // Qna의 Q 수정
    public void update(QnaDTO qnaDTO){
        // 데이터의 id를 조회
        Optional<QnaEntity> qnaEntity = qnaRepository.findById(qnaDTO.getIdx());

        if (qnaEntity.isPresent()){ // 존재하면 수정
            QnaEntity qnaEntitys = modelMapper.map(qnaDTO, QnaEntity.class);
            qnaRepository.save(qnaEntitys);
        }
    }

    // Qna의 Q 삭제
    public void delete(Integer idx){
        qnaRepository.deleteById(idx);
    }

    // Qna의 Q 의 전체목록, 데이터를 화면에 출력
    public List<QnaDTO> list(){
        // 모두 조회
        List<QnaEntity> qnaEntities = qnaRepository.findAll();
        // DTO로 변환 후 배열에 저장
        List<QnaDTO> qnaDTOS = Arrays.asList(modelMapper.map(qnaEntities, QnaDTO[].class));
        return qnaDTOS;
    }

    // Qna의 Q의 개별정보, 게시글 번호의 데이터를 화면에 출력
    public QnaDTO read(Integer idx){
        Optional<QnaEntity> qnaEntity = qnaRepository.findById(idx);

        QnaDTO qnaDTO = modelMapper.map(qnaEntity, QnaDTO.class);
        return qnaDTO;
    }
}

