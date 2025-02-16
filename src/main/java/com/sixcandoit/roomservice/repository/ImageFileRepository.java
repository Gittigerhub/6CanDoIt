package com.sixcandoit.roomservice.repository;

import com.sixcandoit.roomservice.entity.ImageFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFileEntity, Integer> {

    // 조직의 이미지들을 조회
    @Query("select i from ImageFileEntity i where i.organizationJoin.idx = :idx")
    List<ImageFileEntity> organizationJoin(@Param("idx") Integer idx);

    // 객실의 이미지들을 조회
    @Query("select i from ImageFileEntity i where i.roomJoin.idx = :idx")
    List<ImageFileEntity> roomJoin(@Param("idx") Integer idx);

    // 공지의 이미지들을 조회
    @Query("select i from ImageFileEntity i where i.noticeJoin.idx = :idx")
    List<ImageFileEntity> noticeJoin(@Param("idx") Integer idx);

    // qna의 이미지들을 조회
    @Query("select i from ImageFileEntity i where i.qnaJoin.idx = :idx")
    List<ImageFileEntity> qnaJoin(@Param("idx") Integer idx);

    // 메뉴의 이미지들을 조회
    @Query("select i from ImageFileEntity i where i.menuJoin.idx = :idx")
    List<ImageFileEntity> menuJoin(@Param("idx") Integer idx);

    // 광고의 이미지들을 조회
    @Query("select i from ImageFileEntity i where i.advertisementJoin.idx = :idx")
    List<ImageFileEntity> advertisementJoin(@Param("idx") Integer idx);

    // 이벤트의 이미지들을 조회
    @Query("select i from ImageFileEntity i where i.eventJoin.idx = :idx")
    List<ImageFileEntity> eventJoin(@Param("idx") Integer idx);

}