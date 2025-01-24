package com.sixcandoit.roomservice.repository.qna;

import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaRepository extends JpaRepository<QnaEntity, Integer> {

    // 제목만
    @Query("SELECT u FROM QnaEntity u WHERE "+
            " u.qnaTitle like %:keyword%")
    Page<QnaEntity> searchQnaTitle(@Param("keyword") String keyword, Pageable page);

    // 내용만
    @Query("SELECT u FROM QnaEntity u WHERE "+
            " u.qnaContents like %:keyword%")
    Page<QnaEntity> searchQnaContents(@Param("keyword") String keyword, Pageable page);

    // 모든 항목에서 조회
    @Query("SELECT u FROM QnaEntity u WHERE "+
            " u.qnaTitle like %:keyword% or u.qnaContents like %:keyword%")
    Page<QnaEntity> searchAll(@Param("keyword") String keyword, Pageable page);
}
