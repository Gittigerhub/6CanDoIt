package com.sixcandoit.roomservice.service.room;

import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class RoomService {

    // final 선언, 모델 맵퍼 선언
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    // 룸 등록
    public void roomRegister(RoomDTO roomDTO){
        // DTO로 Entity 변환
        RoomEntity roomEntity = modelMapper.map(roomDTO, RoomEntity.class);
        // 저장
        roomRepository.save(roomEntity);
    }

    // 룸 수정
    // 룸 삭제
    // 룸 목록
    // 룸 상세보기
}
