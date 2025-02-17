package com.sixcandoit.roomservice.repository.member;

import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    Optional<MemberEntity> findByIdx(Integer idx);

    //로그인 작업순서
    //1. id가 존재하는지 검색 (존재하지 않는 아이디입니다.)
    //2. 조회한 결과의 비밀번호와 입력한 비빌번호가 일치하면 로그인, 아니면 로그아웃(비밀번호가 틀립니다.)
    Optional<MemberEntity> findByMemberEmail(String memberEmail);

    //아이디와 비밀번호 조회(개별조회)
    Optional<MemberEntity> findByMemberEmailAndPassword(String memberEmail, String password);

    // 삭제
    void deleteByMemberEmail(String memberEmail);

    //회원목록 조회(다중조회)-입력받는 변수가 많은 경우 쿼리로 작성해서 변수의 수를 줄여서 사용
    @Query("SELECT m FROM MemberEntity m WHERE m.memberEmail like %:keyword% or m.memberName like %:keyword%")
    Page<MemberEntity> search(String keyword, Pageable pageable);

    // 이메일 중복 확인 메서드 추가
    boolean existsByMemberEmail(String memberEmail);  // 이메일이 존재하면 true 반환


    // 일반 회원 목록
    // 이름만
    @Query("SELECT u FROM MemberEntity u WHERE "+
            " u.memberName like %:keyword%")
    Page<MemberEntity> searchMemberName(@Param("keyword") String keyword, Pageable page);

    // 이메일만
    @Query("SELECT u FROM MemberEntity u WHERE "+
            " u.memberEmail like %:keyword%")
    Page<MemberEntity> searchMemberEmail(@Param("keyword") String keyword, Pageable page);

    // 연락처만
    @Query("SELECT u FROM MemberEntity u WHERE "+
            " u.memberPhone like %:keyword%")
    Page<MemberEntity> searchMemberPhone(@Param("keyword") String keyword, Pageable page);


}
