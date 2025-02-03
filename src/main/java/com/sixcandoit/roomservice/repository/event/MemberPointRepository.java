package com.sixcandoit.roomservice.repository.event;

import com.sixcandoit.roomservice.dto.event.MemberPointDTO;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.office.ShopDetailEntity;
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





}
