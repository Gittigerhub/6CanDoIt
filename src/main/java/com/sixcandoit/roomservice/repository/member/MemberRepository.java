package com.sixcandoit.roomservice.repository.member;

import com.sixcandoit.roomservice.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
    //로그인 작업순서
    //1. id가 존재하는지 검색 (존재하지 않는 아이디입니다.)
    //2. 조회한 결과의 비밀번호와 입력한 비빌번호가 일치하면 로그인, 아니면 로그아웃(비밀번호가 틀립니다.)
    Optional<MemberEntity> findBymemberEmail(String memberEmail);
}
