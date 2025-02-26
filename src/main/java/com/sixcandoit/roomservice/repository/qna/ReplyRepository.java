package com.sixcandoit.roomservice.repository.qna;

import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import com.sixcandoit.roomservice.entity.qna.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {

    // qna와 조인
    Optional<ReplyEntity> findByQnaJoin(QnaEntity qnaEntity);
    // 질문에 달린 모든 답변을 삭제
    void deleteByQnaJoin(QnaEntity qnaEntity);
}
