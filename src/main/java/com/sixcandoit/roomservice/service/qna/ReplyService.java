package com.sixcandoit.roomservice.service.qna;

import com.sixcandoit.roomservice.dto.qna.QnaDTO;
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

import java.util.List;
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

        ReplyEntity replyEntity =
                modelMapper.map(replyDTO, ReplyEntity.class);
        replyEntity.setQnaJoin(qnaEntity);

        replyRepository.save(replyEntity);
    }

    // Qna의 A 읽기
    public ReplyDTO replyRead(Integer idx){
        return null;
    }

    // Qna의 A 수정
    public ReplyDTO replyUpdate(ReplyDTO replyDTO){
        return null;
    }

    // Qna의 A 삭제
    public void replyDelete(Long rno){
    }

    // Qna의 A 목록
    public List<ReplyDTO> replyList(){
        return null;
    }


}