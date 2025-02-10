package com.sixcandoit.roomservice.service.room;

import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        // 룸 기본 값:
        roomEntity.setRoomView("N"); // 창문 없음
        roomEntity.setRoomSeason("off"); // 비성수기
        roomEntity.setRoomCondition("Y"); // 청소 완료

        // 저장
        roomRepository.save(roomEntity);
    }

    // 룸 수정
    public void roomUpdate(RoomDTO roomDTO){
        // 룸 데이터의 idx를 조회
        log.info("룸 데이터의 idx를 조회한다...");
        Optional<RoomEntity> roomEntity = roomRepository.findById(roomDTO.getIdx());

        if (roomEntity.isPresent()){ // RoomEntity가 존재하면
            // DTO로 Entity 변환
            RoomEntity roomEntitys = modelMapper.map(roomDTO, RoomEntity.class);
            log.info("저장을 수행한다...");
            // 저장
            roomRepository.save(roomEntitys);

        } else {
            throw new IllegalStateException("수정할 Room이 존재하지 않습니다.");
        }
    }

    // 룸 삭제
    public void roomDelete(Integer idx){
        roomRepository.deleteById(idx);
    }

    // 룸 목록
    public List<RoomDTO> roomList(){
        // 모두 조회
        List<RoomEntity> roomEntities = roomRepository.findAll();
        // DTO로 변환 -> 배열에 저장 -> List 변환
        List<RoomDTO> roomDTOS = Arrays.asList(modelMapper.map(roomEntities, RoomDTO[].class));

        return roomDTOS;
    }

    // 룸 상세보기
    public RoomDTO roomDetail(Integer idx){
        Optional<RoomEntity> roomEntity = roomRepository.findById(idx);

        RoomDTO roomDTO = modelMapper.map(roomEntity, RoomDTO.class);

        return roomDTO;
    }
}
