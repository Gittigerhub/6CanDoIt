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

    // 관리자가 답변한 QnA 목록 조회
    @Query(value = "SELECT DISTINCT q FROM QnaEntity q INNER JOIN q.replyList r WHERE r.admin.idx = :adminIdx ORDER BY q.favYn DESC, q.idx DESC",
           countQuery = "SELECT COUNT(DISTINCT q) FROM QnaEntity q INNER JOIN q.replyList r WHERE r.admin.idx = :adminIdx")
    Page<QnaEntity> findQnasByAdminReplies(@Param("adminIdx") Integer adminIdx, Pageable page);

    // 제목으로 검색
    @Query("SELECT q FROM QnaEntity q WHERE q.qnaTitle LIKE %:keyword%")
    Page<QnaEntity> searchQnaTitle(@Param("keyword") String keyword, Pageable page);

    // 내용으로 검색
    @Query("SELECT q FROM QnaEntity q WHERE q.qnaContents LIKE %:keyword%")
    Page<QnaEntity> searchQnaContents(@Param("keyword") String keyword, Pageable page);

    // 제목+내용으로 검색
    @Query("SELECT q FROM QnaEntity q WHERE q.qnaTitle LIKE %:keyword% OR q.qnaContents LIKE %:keyword%")
    Page<QnaEntity> searchQnaAll(@Param("keyword") String keyword, Pageable page);

    // 답변만 검색
    @Query("SELECT q FROM QnaEntity q JOIN q.replyList r WHERE r.replyContents LIKE %:keyword%")
    Page<QnaEntity> searchReplyAll(@Param("keyword") String keyword, Pageable page);

    // 자주 묻는 질문만 검색
    @Query("SELECT q FROM QnaEntity q WHERE q.favYn = 'Y'")
    Page<QnaEntity> searchFavYn(Pageable page);

    // 미답변만 검색
    @Query("SELECT q FROM QnaEntity q WHERE q.replyYn = 'N'")
    Page<QnaEntity> searchUnreplied(Pageable page);

    // 답변완료만 검색
    @Query("SELECT q FROM QnaEntity q WHERE q.replyYn = 'Y'")
    Page<QnaEntity> searchReplied(Pageable page);

    // 전체 검색
    @Query("SELECT DISTINCT q FROM QnaEntity q LEFT JOIN q.replyList r WHERE q.qnaTitle LIKE %:keyword% OR q.qnaContents LIKE %:keyword% OR r.replyContents LIKE %:keyword%")
    Page<QnaEntity> searchQnaAndReply(@Param("keyword") String keyword, Pageable page);
}
