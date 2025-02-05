package com.sixcandoit.roomservice.repository.event;

import com.sixcandoit.roomservice.entity.event.EventEntity;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer> {







}
