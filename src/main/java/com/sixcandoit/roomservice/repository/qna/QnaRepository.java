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

    // 제목+내용
    @Query("SELECT u FROM QnaEntity u WHERE "+
            " u.qnaTitle like %:keyword% or u.qnaContents like %:keyword%")
    Page<QnaEntity> searchQnaAll(@Param("keyword") String keyword, Pageable page);

    // 답변만
    @Query("SELECT q FROM QnaEntity q LEFT JOIN q.replyList r WHERE "+
            " r.replyTitle like %:keyword% or r.replyContents like %:keyword%")
    Page<QnaEntity> searchReplyAll(@Param("keyword") String keyword, Pageable page);

    // 모든 항목에서
    @Query("SELECT q FROM QnaEntity q LEFT JOIN q.replyList r WHERE "+
            " (q.qnaTitle LIKE %:keyword% OR q.qnaContents LIKE %:keyword%) "+
            " OR (r.replyTitle LIKE %:keyword% OR r.replyContents LIKE %:keyword%)")
    Page<QnaEntity> searchQnaAndReply(@Param("keyword") String keyword, Pageable page);

    // 자주 묻는 질문
    @Query("SELECT q FROM QnaEntity q LEFT JOIN q.replyList r WHERE " +
            "(q.favYn = 'Y')")
    Page<QnaEntity> searchFavYn(Pageable page);

    // 미답변 QnA 검색
    @Query("SELECT q FROM QnaEntity q WHERE q.replyYn = 'N'")
    Page<QnaEntity> searchUnreplied(Pageable page);

    // 답변완료 QnA 검색
    @Query("SELECT q FROM QnaEntity q WHERE q.replyYn = 'Y'")
    Page<QnaEntity> searchReplied(Pageable page);

}
