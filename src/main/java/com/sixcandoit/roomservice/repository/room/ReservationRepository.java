package com.sixcandoit.roomservice.repository.room;

import com.sixcandoit.roomservice.entity.room.ReservationEntity;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {

    //입력한 2개의 날짜가 시작날짜에 속하는 값을 조회
    Page<ReservationEntity> findByStartDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // 첫번째 날짜가 시작날짜에 속하고, 두번째 날짜가 끝날짜에 속하는 값을 조회
    List<ReservationEntity> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDate startDate, LocalDate endDate);

    //페이지시
    //Page<ReserveEntity> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDate startDate, LocalDate endDate, Pageable pageable);

    //JPQL 쿼리
    @Query("SELECT r FROM ReservationEntity r WHERE r.startDate >= :startDate AND r.endDate <= :endDate")
    List<ReservationEntity> findByStartDateAfterAndEndDateBefore(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

//    // JPQL 쿼리: 새로 예약하려는 날짜 범위와 겹치는 예약을 찾기 위한 쿼리
//    @Query("SELECT r FROM ReservationEntity r WHERE r.startDate < :endDate AND r.endDate > :startDate")
//    List<ReservationEntity> findOverlappingReservations(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // JPQL 쿼리: 특정 객실에 대해 새로 예약하려는 날짜 범위와 겹치는 예약을 찾기 위한 쿼리
    @Query("SELECT r FROM ReservationEntity r WHERE r.roomJoin.idx = :roomIdx " +
            "AND r.startDate < :endDate AND r.endDate > :startDate")
    List<ReservationEntity> findOverlappingReservations(@Param("roomIdx") Integer roomIdx,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate);


    @Query("SELECT r FROM RoomEntity r WHERE r.idx NOT IN (" +
            "SELECT rr.roomJoin.idx FROM ReservationEntity rr WHERE " +
            "(rr.startDate < :endDate AND rr.endDate > :startDate))")
    List<RoomEntity> findAvailableRooms(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Optional<ReservationEntity> findByIdx(Integer idx);

    // 특정 organization의 room들에 대한 예약 조회
    List<ReservationEntity> findByRoomJoin_OrganizationJoin_Idx(Integer organ_idx);

    // 특정 organization의 room들에 대한 예약을 날짜 범위로 조회
    @Query("SELECT r FROM ReservationEntity r WHERE r.roomJoin.organizationJoin.idx = :organ_idx " +
            "AND r.startDate >= :startDate AND r.endDate <= :endDate")
    List<ReservationEntity> findByRoomJoin_OrganizationJoin_IdxAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            @Param("organ_idx") Integer organ_idx,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
