package com.sixcandoit.roomservice.repository.room;

import com.sixcandoit.roomservice.entity.room.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Integer> {
    Optional<RoomEntity> findByIdx(Integer idx);

}
