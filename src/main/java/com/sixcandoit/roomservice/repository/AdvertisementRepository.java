package com.sixcandoit.roomservice.repository;

import com.sixcandoit.roomservice.entity.AdvertisementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Integer> {


    // 전체(제목 + 조직명)
    @Query("select a from AdvertisementEntity a")
    Page<AdvertisementEntity> searchAll( Pageable pageable);

    // 제목 + 키워드
    @Query("select a from AdvertisementEntity a where a.adTitle like %:keyword%")
    Page<AdvertisementEntity> searchTitle(@Param("keyword") String keyword, Pageable pageable);

    // 조직명 + 키워드
    @Query("select a from AdvertisementEntity a where a.organizationJoin.organName like %:keyword%")
    Page<AdvertisementEntity> searchOrgan(@Param("keyword") String keyword, Pageable pageable);

    // 전체(제목 + 조직명) + 키워드
    @Query("select a from AdvertisementEntity a where a.adTitle like %:keyword% or a.organizationJoin.organName like %:keyword%")
    Page<AdvertisementEntity> searchAllName(@Param("keyword") String keyword, Pageable pageable);

    // 활성화된 광고
    @Query("select a from AdvertisementEntity a where  a.adState = 'Y'")
    List<AdvertisementEntity> adPick();

    // 조회수 증가 쿼리
    @Modifying // 이 설정을 안해주면 기본적으로 select문으로 쿼리를 실행하려하여 오류남
    @Query("UPDATE AdvertisementEntity a SET a.adHits = a.adHits + 1 WHERE a.idx = :idx")
    int incrementAdHits(@Param("idx") Integer idx);


}