package com.sixcandoit.roomservice.repository.admin;

import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Integer> {
    //로그인 작업순서
    //1. id가 존재하는지 검색 (존재하지 않는 아이디입니다.)
    //2. 조회한 결과의 비밀번호와 입력한 비빌번호가 일치하면 로그인, 아니면 로그아웃(비밀번호가 틀립니다.)
    Optional<AdminEntity> findByAdminEmail(String adminEmail);

    //아이디와 비밀번호 조회(개별조회)
    Optional<AdminEntity> findByAdminEmailAndAdminPwd(String adminEmail, String adminPwd);

    //회원목록 조회(다중조회)-입력받는 변수가 많은 경우 쿼리로 작성해서 변수의 수를 줄여서 사용
    @Query("SELECT a FROM AdminEntity a WHERE a.adminEmail like %:keyword% or a.adminName like %:keyword%")
    Page<AdminEntity> search(String keyword, Pageable pageable);

    // =====================================================================================
    // 조직 검색
    // 결과값이 여러개이면 List<AdminEntity>, Page<AdminEntity>
    // 결과값이 하나이면 AdminEntity, Optional<AdminEntity>

    // 조직명 + 본사
    @Query("SELECT a from AdminEntity a where a.organizationJoin.organName like %:keyword% and a.organizationJoin.organType like 'HO'")
    Page<AdminEntity> searchHOKey(@Param("keyword") String keyword, Pageable pageable);

    // 조직명 + 지사
    @Query("SELECT a from AdminEntity a where a.organizationJoin.organName like %:keyword% and a.organizationJoin.organType like 'BO'")
    Page<AdminEntity> searchBOKey(@Param("keyword") String keyword, Pageable pageable);

    // 조직명 + 매장
    @Query("SELECT a from AdminEntity a where a.organizationJoin.organName like %:keyword% and a.organizationJoin.organType like 'SHOP'")
    Page<AdminEntity> searchSHOPKey(@Param("keyword") String keyword, Pageable pageable);

    // 본사
    @Query("SELECT a from AdminEntity a where a.organizationJoin.organType like 'HO'")
    Page<AdminEntity> searchHO(Pageable pageable);

    // 지사
    @Query("SELECT a from AdminEntity a where a.organizationJoin.organType like 'BO'")
    Page<AdminEntity> searchBO(Pageable pageable);

    // 조직명 + 매장
    @Query("SELECT a from AdminEntity a where a.organizationJoin.organType like 'SHOP'")
    Page<AdminEntity> searchSHOP(Pageable pageable);

    // 조직 테이블과 참조된 관리자 전체
    @Query("SELECT a from AdminEntity a where a.organizationJoin.idx != null")
    Page<AdminEntity> searchAll(Pageable pageable);

    //회원목록 조회
    @Query("SELECT a FROM AdminEntity a WHERE a.adminEmail like %:keyword% or a.adminName like %:keyword% and a.organizationJoin.idx = null")
    List<AdminEntity> searchAdminKey(String searchAdmin);

}
