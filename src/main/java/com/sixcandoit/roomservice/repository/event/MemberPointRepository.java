package com.sixcandoit.roomservice.repository.event;

import com.sixcandoit.roomservice.dto.event.MemberPointDTO;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.office.ShopDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberPointRepository extends JpaRepository<MemberPointEntity, Integer> {

    // memberEntity의 idx로 포인트 목록 조회
    List<MemberPointEntity> findByMemberJoinIdx(Integer idx);

    // memeberpoint 페이지 네이션 검색 조회
    //회원 이메일로 검색
    @Query("SELECT m FROM MemberPointEntity m WHERE m.memberJoin.memberEmail LIKE %:keyword%")
    Page<MemberPointEntity> findByEmail(@Param("keyword") String keyword, Pageable pageable);

    //회원 이름으로 검색
    @Query("SELECT m FROM MemberPointEntity m WHERE m.memberJoin.memberName LIKE %:keyword")
    Page<MemberPointEntity> findByName(@Param("keyword") String keyword, Pageable pageable);

    /*  여기는 userMemberpoint   */
    //회원 이름 또는 내용으로 검색
    @Query("SELECT m FROM MemberPointEntity m WHERE m.memberJoin.memberEmail LIKE %:keyword OR  m.memberJoin.memberName LIKE %:keyword")
    Page<MemberPointEntity> findByNameOrEmail(@Param("keyword") String keyword, Pageable pageable);

    //포인트 내용으로 검색 
    @Query("SELECT m FROM MemberPointEntity m WHERE m.memberPointContents LIKE CONCAT('%', :keyword, '%')")
    Page<MemberPointEntity> findByContents(@Param("keyword") String keyword, Pageable pageable);

    //포인트 사용여부로 검색
    @Query("SELECT m FROM MemberPointEntity m WHERE m.memberPointOperationYn LIKE %:keyword")
    Page<MemberPointEntity> findByPointOperationYn(@Param("keyword") String keyword, Pageable pageable);

    //포인트 내용 또는 사용 여부로 검색
    @Query("SELECT m FROM MemberPointEntity m WHERE m.memberPointContents LIKE %:keyword OR  m.memberPointOperationYn LIKE %:keyword")
    Page<MemberPointEntity> findByContentsOrOperationYn(@Param("keyword") String keyword, Pageable pageable);





}
