package com.sixcandoit.roomservice.repository.office;

import com.sixcandoit.roomservice.entity.office.ShopDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopDetailRepository extends JpaRepository<ShopDetailEntity, Integer> {

    // 조직 IDX로 데이터 조회

    @Query("select s from ShopDetailEntity s where s.organizationJoin.idx = :idx")
    Optional<ShopDetailEntity> findOrgan(@Param("idx") Integer idx);

}