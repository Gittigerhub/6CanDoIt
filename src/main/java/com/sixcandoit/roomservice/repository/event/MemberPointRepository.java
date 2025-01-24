package com.sixcandoit.roomservice.repository.event;

import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import com.sixcandoit.roomservice.entity.office.ShopDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberPointRepository extends JpaRepository<MemberPointEntity, Integer> {

    // memberEntity의 idx로 포인트 목록 조회
    List<MemberPointEntity> findByMemberJoinIdx(Integer idx);

}
