package com.sixcandoit.roomservice.repository.event;

import com.sixcandoit.roomservice.entity.event.EventEntity;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer> {

    //포인트 내용으로 검색
    @Query("SELECT e FROM EventEntity e WHERE e.eventContents LIKE CONCAT('%', :keyword, '%')")
    Page<EventEntity> findByContents(@Param("keyword") String keyword, Pageable pageable);

    //포인트 사용여부로 검색
    @Query("SELECT e FROM EventEntity e WHERE e.activeYn LIKE %:keyword")
    Page<EventEntity> findByActiveYn(@Param("keyword") String keyword, Pageable pageable);

    //포인트 기간으로 검색
    @Query("SELECT e FROM EventEntity e WHERE e.eventEndDate <=:endDate  AND e.eventStartDate >=:startDate")
    Page<EventEntity> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);




}
