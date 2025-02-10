package com.sixcandoit.roomservice.repository.notice;

import com.sixcandoit.roomservice.entity.notice.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends org.springframework.data.jpa.repository.JpaRepository<NoticeEntity, Integer> {

    // 제목 검색
    @Query("SELECT n FROM NoticeEntity n WHERE n.noticeTitle LIKE %:keyword%")
    Page<NoticeEntity> searchNoticeTitle(@Param("keyword") String keyword, Pageable pageable);

    // 내용 검색
    @Query("SELECT n FROM NoticeEntity n WHERE n.noticeContents LIKE %:keyword%")
    Page<NoticeEntity> searchNoticeContents(@Param("keyword") String keyword, Pageable pageable);

    // 제목 또는 내용 검색
    @Query("SELECT n FROM NoticeEntity n WHERE n.noticeTitle LIKE %:keyword% OR n.noticeContents LIKE %:keyword%")
    Page<NoticeEntity> searchAll(@Param("keyword") String keyword, Pageable pageable);
}