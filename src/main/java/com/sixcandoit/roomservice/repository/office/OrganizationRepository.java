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

    // 사용자 호텔 조회

    // 전체
    @Query("SELECT o FROM OrganizationEntity o where o.organType = 'HO' or o.organType = 'BO'")
    List<OrganizationEntity> searchALLList();

    // 전체
    @Query("SELECT o FROM OrganizationEntity o WHERE (o.organType = 'HO' or o.organType = 'BO') and o.organName like %:keyword%")
    List<OrganizationEntity> searchALLNameList(String keyword);

    // 특정 지역 선택시
    @Query("SELECT o FROM OrganizationEntity o where (o.organType = 'HO' or o.organType = 'BO') and o.organAddress like :location% and o.organAddress like %:location2%")
    List<OrganizationEntity> searchHotels(String location, String location2);

    // 특정 지역 선택시 + 호텔명
    @Query("SELECT o FROM OrganizationEntity o WHERE (o.organType = 'HO' or o.organType = 'BO') and o.organName like %:keyword% and " +
            "o.organAddress like :location% and o.organAddress like %:location2%")
    List<OrganizationEntity> searchHotelsName(String location, String location2, String keyword);


    // ================================================

    // 조직 검색

    // 전체(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o where o.organType = 'HO' or o.organType = 'BO'")
    Page<OrganizationEntity> searchALL(Pageable pageable);

    // 본사(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'HO'")
    Page<OrganizationEntity> searchHO(Pageable pageable);

    // 지사(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'BO'")
    Page<OrganizationEntity> searchBO(Pageable pageable);

    // 매장(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'SHOP'")
    Page<OrganizationEntity> searchSHOP(Pageable pageable);

    // 전체 + 조직명(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE (o.organType = 'HO' or o.organType = 'BO') and o.organName like %:keyword%")
    Page<OrganizationEntity> searchALLName(@Param("keyword") String keyword, Pageable pageable);

    // 본사 + 조직명(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organName like %:keyword% and o.organType = 'HO'")
    Page<OrganizationEntity> searchHOName(@Param("keyword") String keyword, Pageable pageable);

    // 지사 + 조직명(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organName like %:keyword% and o.organType = 'BO'")
    Page<OrganizationEntity> searchBOName(@Param("keyword") String keyword, Pageable pageable);

    // 매장 + 조직명(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organName like %:keyword% and o.organType = 'SHOP'")
    Page<OrganizationEntity> searchSHOPName(@Param("keyword") String keyword, Pageable pageable);

    // ==============================

    // 관리자별 하위 검색

    // 전체(본사 관리자)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.head.idx = :adminIdx or o.hotels.idx = :adminIdx")
    Page<OrganizationEntity> searchHOA(Pageable pageable, Integer adminIdx);

    // 지사(본사 관리자)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'BO' and o.head.idx = :adminIdx")
    Page<OrganizationEntity> searchHOB(Pageable pageable, Integer adminIdx);

    // 매장(본사 관리자)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'SHOP' and o.hotels.idx = :adminIdx")
    Page<OrganizationEntity> searchHOS(Pageable pageable, Integer adminIdx);

    // 전체(본사 관리자) + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE (o.head.idx = :adminIdx or o.hotels.idx = :adminIdx) and o.organName like %:keyword%")
    Page<OrganizationEntity> searchHOAName(@Param("keyword") String keyword, Pageable pageable, Integer adminIdx);

    // 지사(본사 관리자) + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'BO' and o.head.idx = :adminIdx and o.organName like %:keyword%")
    Page<OrganizationEntity> searchHOBName(@Param("keyword") String keyword, Pageable pageable, Integer adminIdx);

    // 매장(본사 관리자) + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'SHOP' and o.hotels.idx = :adminIdx and o.organName like %:keyword%")
    Page<OrganizationEntity> searchHOSName(@Param("keyword") String keyword, Pageable pageable, Integer adminIdx);

    // ---------------

    // 전체(지사 관리자)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.hotels.idx = :adminIdx")
    Page<OrganizationEntity> searchBOA(Pageable pageable, Integer adminIdx);

    // 매장(지사 관리자)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'SHOP' and o.hotels.idx = :adminIdx")
    Page<OrganizationEntity> searchBOS(Pageable pageable, Integer adminIdx);

    // 전체(지사 관리자) + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE o.hotels.idx = :adminIdx and o.organName like %:keyword%")
    Page<OrganizationEntity> searchBOAName(@Param("keyword") String keyword, Pageable pageable, Integer adminIdx);

    // 매장(지사 관리자) + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'SHOP' and o.hotels.idx = :adminIdx and o.organName like %:keyword%")
    Page<OrganizationEntity> searchBOSName(@Param("keyword") String keyword, Pageable pageable, Integer adminIdx);

    // ===============================

    // 관리자 메뉴 등록시 조직 조회하기
    @Query("SELECT o FROM OrganizationEntity o WHERE o.hotels.idx = :idx")
    OrganizationEntity findHotels(@Param("idx") Integer idx);

    // ===============================

    // 상위조직 검색 List

    // 본사(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'HO'")
    List<OrganizationEntity> findHO();

    // 지사(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'BO'")
    List<OrganizationEntity> findBO();

    // 본사 + 조직명(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organName like %:keyword% and o.organType = 'HO'")
    List<OrganizationEntity> findHOName(@Param("keyword") String keyword);

    // 지사 + 조직명(슈퍼 바이저)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organName like %:keyword% and o.organType = 'BO'")
    List<OrganizationEntity> findBOName(@Param("keyword") String keyword);

    // ==============================

    // 관리자별 하위 검색

    // 본사(본사 관리자)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'HO' and o.idx = :adminIdx")
    List<OrganizationEntity> findHOH(Integer adminIdx);

    // 지사(본사 관리자)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'BO' and o.head.idx = :adminIdx")
    List<OrganizationEntity> findHOB(Integer adminIdx);

    // 본사(본사 관리자) + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'HO' and o.idx = :adminIdx and o.organName like %:keyword%")
    List<OrganizationEntity> findHOHName(@Param("keyword") String keyword, Integer adminIdx);

    // 지사(본사 관리자) + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'BO' and o.head.idx = :adminIdx and o.organName like %:keyword%")
    List<OrganizationEntity> findHOBName(@Param("keyword") String keyword, Integer adminIdx);

    // ---------------

    // 지사(지사 관리자)
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'BO' and o.idx = :adminIdx")
    List<OrganizationEntity> findBOB(Integer adminIdx);

    // 지사(지사 관리자) + 조직명
    @Query("SELECT o FROM OrganizationEntity o WHERE o.organType = 'BO' and o.idx = :adminIdx and o.organName like %:keyword%")
    List<OrganizationEntity> findBOBName(@Param("keyword") String keyword, Integer adminIdx);

}