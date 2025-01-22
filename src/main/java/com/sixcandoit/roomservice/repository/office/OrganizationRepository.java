package com.sixcandoit.roomservice.repository.office;

import com.sixcandoit.roomservice.entity.member.AdminEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Integer> {

    // 분류(총판명 총판ID, 총판장ID, 총판장), 키워드(찾을 내용)
    // 본사 + 조직명, 지사 + 조직명, 매장 + 조직명
    // 결과값이 여러개이면 List<StoreEntity>, Page<StoreEntity>
    // 결과값이 하나이면 StoreEntity, Optional<StoreEntity>

    // 본사 + 조직명
    // OrganizationEntity o(별칭) : 별칭은 테이블명을 약식표기(알파벳 1글자 지정)
    // String keyword => @Param("keyword") => :keyword
    @Query("SELECT o from OrganizationEntity o where o.organName like %:keyword% and o.organType like 'HO'")
    Page<OrganizationEntity> searchHO(@Param("keyword") String keyword, Pageable pageable);

    // 지사 + 조직명
    @Query("SELECT o from OrganizationEntity o where o.organName like %:keyword% and o.organType like 'BO'")
    Page<OrganizationEntity> searchBO(@Param("keyword") String keyword, Pageable pageable);

    // 매장 + 조직명
    @Query("SELECT o from OrganizationEntity o where o.organName like %:keyword% and o.organType like 'SHOP'")
    Page<OrganizationEntity> searchSHOP(@Param("keyword") String keyword, Pageable pageable);

}