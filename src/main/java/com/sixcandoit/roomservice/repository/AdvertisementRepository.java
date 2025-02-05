package com.sixcandoit.roomservice.repository;

import com.sixcandoit.roomservice.entity.AdvertisementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Integer> {

    // 제목
    @Query("select a from AdvertisementEntity a where a.adTitle like %:keyword%")
    Page<AdvertisementEntity> searchTitle(@Param("keyword") String keyword, Pageable pageable);

    // 조직명
    @Query("select a from AdvertisementEntity a where a.organizationJoin.organName like %:keyword%")
    Page<AdvertisementEntity> searchOrgan(@Param("keyword") String keyword, Pageable pageable);

    // 전체(제목 + 조직명)
    @Query("select a from AdvertisementEntity a where a.adTitle like %:keyword% or a.organizationJoin.organName like %:keyword%")
    Page<AdvertisementEntity> searchAll(@Param("keyword") String keyword, Pageable pageable);

}