package com.sixcandoit.roomservice.repository.qna;

import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import com.sixcandoit.roomservice.entity.qna.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {

    Optional<ReplyEntity> findByQnaJoin(QnaEntity qnaEntity);

}
