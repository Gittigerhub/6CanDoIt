package com.sixcandoit.roomservice.service.qna;

import com.sixcandoit.roomservice.dto.qna.ReplyDTO;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import com.sixcandoit.roomservice.entity.qna.ReplyEntity;
import com.sixcandoit.roomservice.repository.qna.QnaRepository;
import com.sixcandoit.roomservice.repository.qna.ReplyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class ReplyService {

    // final 선언, 모델 맵퍼 선언
    private final ReplyRepository replyRepository;
    private final QnaRepository qnaRepository;
    private final ModelMapper modelMapper;

    // Qna의 A 쓰기
    public void replyRegister(ReplyDTO replyDTO) {
        Optional<QnaEntity> QnaEntity =
                qnaRepository.findById(replyDTO.getQnaIdx());

        QnaEntity qnaEntity = QnaEntity.orElseThrow(EntityNotFoundException::new);
        // 없을 시 예외처리

        // 이미 해당 질문에 답변이 존재하는지 확인
        Optional<ReplyEntity> replyEntity = replyRepository.findByQnaJoin(qnaEntity);
        if (replyEntity.isPresent()) {
            throw new IllegalStateException("이 질문에는 이미 답변이 존재합니다.");
        }

        // 답변이 없으면 새로운 답변 등록
        ReplyEntity replyEntitys = modelMapper.map(replyDTO, ReplyEntity.class);
        replyEntitys.setQnaJoin(qnaEntity);

        replyRepository.save(replyEntitys);
    }

    // 답변 수정
    public void replyUpdate(ReplyDTO replyDTO) {
        // Qna idx 조회
        log.info("qna있는지조회한다...");
        Optional<QnaEntity> QnaEntity =
                qnaRepository.findById(replyDTO.getQnaIdx());

        // Qna 없을 시 예외처리
        QnaEntity qnaEntity = QnaEntity.orElseThrow(EntityNotFoundException::new);

        log.info("답변이있는지조회한다...");
        // 해당 질문에 답변이 존재하는지 확인
        Optional<ReplyEntity> replyEntity = replyRepository.findByQnaJoin(qnaEntity);

        if (replyEntity.isPresent()) { // 존재하면 수정
            log.info("답변이존재하면...");
            ReplyEntity replyEntity1 = replyEntity.get();
            // 기존 답변 삭제
            log.info("기존답변을삭제...");
            replyRepository.delete(replyEntity1);

            log.info("새로수정된답변저장...");
            // 새로 수정된 답변 저장
            ReplyEntity replyEntitys = modelMapper.map(replyDTO, ReplyEntity.class);
            replyEntitys.setQnaJoin(qnaEntity);
            replyRepository.save(replyEntitys);
        }
    }

    // Qna의 A 삭제
    public void replyDelete(Integer idx){
        replyRepository.deleteById(idx);
    }

    // 질문에 달린 모든 답변을 삭제
    public void deleteRepliesByQnaIdx(Integer qnaIdx) {
        // QnaEntity를 조회
        QnaEntity qnaEntity = qnaRepository.findById(qnaIdx)
                .orElseThrow(() -> new RuntimeException("Qna not found"));

        // 해당 QnaEntity에 달린 모든 답변 삭제
        replyRepository.deleteByQnaJoin(qnaEntity);
    }
}