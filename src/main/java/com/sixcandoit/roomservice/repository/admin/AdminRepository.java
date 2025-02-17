package com.sixcandoit.roomservice.repository.admin;

import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Integer> {

    //로그인 작업순서
    //1. id가 존재하는지 검색 (존재하지 않는 아이디입니다.)
    //2. 조회한 결과의 비밀번호와 입력한 비빌번호가 일치하면 로그인, 아니면 로그아웃(비밀번호가 틀립니다.)
    Optional<AdminEntity> findByAdminEmail(String adminEmail);

    //아이디와 비밀번호 조회(개별조회)
    Optional<AdminEntity> findByAdminEmailAndPassword(String adminEmail, String password);

    // 삭제
    void deleteByAdminEmail(String adminEmail);

    // 이메일 중복 확인 메서드 추가
    boolean existsByAdminEmail(String adminEmail);  // 이메일이 존재하면 true 반환

    //회원목록 조회(다중조회)-입력받는 변수가 많은 경우 쿼리로 작성해서 변수의 수를 줄여서 사용
    @Query("SELECT a FROM AdminEntity a WHERE a.adminEmail like %:keyword% or a.adminName like %:keyword%")
    Page<AdminEntity> search(String keyword, Pageable pageable);

}
