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
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
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
        Optional<QnaEntity> optionalQnaEntity =
                qnaRepository.findById(replyDTO.getQnaIdx());

        QnaEntity qnaEntity = optionalQnaEntity.orElseThrow(EntityNotFoundException::new);
        // 없을 시 예외처리

        // 이미 해당 질문에 답변이 존재하는지 확인
        Optional<ReplyEntity> existingReply = replyRepository.findByQnaJoin(qnaEntity);
        if (existingReply.isPresent()) {
            throw new IllegalStateException("이 질문에는 이미 답변이 존재합니다.");
        }

        // 답변이 없으면 새로운 답변 등록
        ReplyEntity replyEntity = modelMapper.map(replyDTO, ReplyEntity.class);
        replyEntity.setQnaJoin(qnaEntity);

        replyRepository.save(replyEntity);
    }

    // 답변 조회
    public ReplyEntity getReply(Integer idx) {
        Optional<ReplyEntity> replyEntity = this.replyRepository.findById(idx);
        if (replyEntity.isPresent()){
            return replyEntity.get();
        }else {
            throw new NotFoundException("답변이 존재하지 않습니다.");
        }
    }

    // 답변 수정
    public void replyUpdate(ReplyEntity replyEntity,
                            String replyTitle,
                            String replyContents){
        replyEntity.setReplyTitle(replyTitle);
        replyEntity.setReplyContents(replyContents);
        replyEntity.setModDate(LocalDateTime.now());
        this.replyRepository.save(replyEntity);
    }

    // Qna의 A 삭제
    public void replyDelete(Integer idx){
        replyRepository.deleteById(idx);
    }
}