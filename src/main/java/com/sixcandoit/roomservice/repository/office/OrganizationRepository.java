package com.sixcandoit.roomservice.repository.office;

import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Integer> {

    // 조직 검색

    // 본사
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'HO'")
    Page<OrganizationEntity> searchHO(Pageable pageable);

    // 지사
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'BO'")
    Page<OrganizationEntity> searchBO(Pageable pageable);

    // 매장
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'SHOP'")
    Page<OrganizationEntity> searchSHOP(Pageable pageable);

    // 본사 + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organName like %:keyword% and o.organType = 'HO'")
    Page<OrganizationEntity> searchHOName(@Param("keyword") String keyword, Pageable pageable);

    // 지사 + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organName like %:keyword% and o.organType = 'BO'")
    Page<OrganizationEntity> searchBOName(@Param("keyword") String keyword, Pageable pageable);

    // 매사 + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organName like %:keyword% and o.organType = 'SHOP'")
    Page<OrganizationEntity> searchSHOPName(@Param("keyword") String keyword, Pageable pageable);

    // ==============================

    // 분류에 따른 조회
    List<OrganizationEntity> findByOrganType(String searchType);

    List<OrganizationEntity> findByOrganTypeAndOrganNameLikeIgnoreCase(String searchType, String searchWord);
    // 본사 + 조직명



}