package com.sixcandoit.roomservice.repository;

import com.sixcandoit.roomservice.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Integer> {

    // 이미지 검색
    // 타입이 <연관테이블> 이면서 <해당테이블 idx> 이거나
    // 타입이 <다른 연관테이블> 이면서 <다른 테이블 idx> 이거나 일때
    @Query("select f from FileEntity f where" +
            "(:type = 'organ' and f.organizationJoin.idx = :idx) or " +
            "(:type = 'room' and f.roomJoin.idx = :idx) or " +
            "(:type = 'adver' and f.advertisementJoin.idx = :idx) or " +
            "(:type = 'event' and f.eventJoin.idx = :idx) or " +
            "(:type = 'menu' and f.menuJoin.idx = :idx) or " +
            "(:type = 'qna' and f.qnaJoin.idx = :idx) or " +
            "(:type = 'notice' and f.noticeJoin.idx = :idx)")
    List<FileEntity> findByType(@Param("type") String type, @Param("idx") Integer idx);
//
//    // 특정 조직의 모든 이미지 조회
//    List<ImageEntity> findByOrganization(OrganizationEntity organization);
//
//    // 특정 조직의 대표 이미지 조회
//    Optional<ImageEntity> findByOrganizationAndRepImageYn(OrganizationEntity organization, String repImageYn);

}