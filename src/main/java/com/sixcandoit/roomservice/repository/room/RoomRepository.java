package com.sixcandoit.roomservice.repository.room;

import com.sixcandoit.roomservice.entity.room.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Integer> {
    Optional<RoomEntity> findByIdx(Integer idx);

    // 룸 타입만
    @Query("SELECT u FROM RoomEntity u WHERE "+
            " u.roomType like %:keyword%")
    Page<RoomEntity> searchRoomType(@Param("keyword") String keyword, Pageable page);

    // 룸 이름만
    @Query("SELECT u FROM RoomEntity u WHERE "+
            " u.roomName like %:keyword%")
    Page<RoomEntity> searchRoomName(@Param("keyword") String keyword, Pageable page);

    // 투숙객 수로 검색
    @Query("SELECT u FROM RoomEntity u WHERE u.roomBreakfast = 'Y'")
    Page<RoomEntity> searchRoomBreakfast(Pageable page);

    // 빈 방 검색
    @Query("SELECT u FROM RoomEntity u WHERE u.resStatus = '1'")
    Page<RoomEntity> searchRes1(Pageable page);

    // 예약중인 방 검색
    @Query("SELECT u FROM RoomEntity u WHERE u.resStatus = '2'")
    Page<RoomEntity> searchRes2(Pageable page);

    // 체크인 방 검색
    @Query("SELECT u FROM RoomEntity u WHERE u.resStatus = '3'")
    Page<RoomEntity> searchRes3(Pageable page);

    // 체크아웃 방 검색
    @Query("SELECT u FROM RoomEntity u WHERE u.resStatus = '4'")
    Page<RoomEntity> searchRes4(Pageable page);

    // 조직 idx로 방 검색
    @Query("SELECT r FROM RoomEntity r WHERE r.organizationJoin.idx = :organIdx")
    Page<RoomEntity> findByOrganIdx(@Param("organIdx") Integer organIdx, Pageable pageable);

    // 조직별 룸 목록 조회 (가격 내림차순)
    List<RoomEntity> findByOrganizationJoin_IdxOrderByRoomPriceDesc(Integer organIdx);

}
