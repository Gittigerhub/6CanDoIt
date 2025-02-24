package com.sixcandoit.roomservice.service.room;

import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
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
        roomEntity.setResStatus("1"); // 예약 상태(1:빈 방, 2:예약, 3:체크인, 4:체크 아웃)

        // 저장
        roomRepository.save(roomEntity);
    }

    // 룸 수정
    public void roomUpdate(RoomDTO roomDTO){
        // 룸 데이터의 idx를 조회
        log.info("룸 데이터의 idx를 조회한다...");
        Optional<RoomEntity> roomEntityOpt = roomRepository.findById(roomDTO.getIdx());

        if (roomEntityOpt.isPresent()) { // RoomEntity가 존재하면
            RoomEntity roomEntity = roomEntityOpt.get();

            // 기존 RoomEntity에서 ResStatus를 그대로 유지
            roomEntity.setRoomName(roomDTO.getRoomName());
            roomEntity.setRoomInfo(roomDTO.getRoomInfo());
            roomEntity.setRoomType(roomDTO.getRoomType());
            roomEntity.setRoomView(roomDTO.getRoomView());
            roomEntity.setRoomNum(roomDTO.getRoomNum());
            roomEntity.setRoomBed(roomDTO.getRoomBed());
            roomEntity.setRoomPrice(roomDTO.getRoomPrice());
            roomEntity.setRoomSize(roomDTO.getRoomSize());
            roomEntity.setRoomBreakfast(roomDTO.getRoomBreakfast());
            roomEntity.setRoomSmokingYn(roomDTO.getRoomSmokingYn());
            roomEntity.setRoomWifi(roomDTO.getRoomWifi());
            roomEntity.setRoomTv(roomDTO.getRoomTv());
            roomEntity.setRoomAir(roomDTO.getRoomAir());
            roomEntity.setRoomBath(roomDTO.getRoomBath());
            roomEntity.setRoomCheckIn(roomDTO.getRoomCheckIn());
            roomEntity.setRoomCheckOut(roomDTO.getRoomCheckOut());

            // 저장
            roomRepository.save(roomEntity);

            log.info("룸 데이터가 업데이트되었습니다.");
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
        // 내림차순으로 roomPrice 기준 정렬
        Sort sort = Sort.by(Sort.Order.desc("roomPrice"));  // 'roomPrice' 필드를 내림차순으로 정렬
        // 모두 조회
        List<RoomEntity> roomEntities = roomRepository.findAll(sort);
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

    // 성수기 업데이트
    public void updateSeason(Integer idx, String roomSeason) {
        // Room 엔티티 조회
        Optional<RoomEntity> roomEntity = roomRepository.findById(idx);

        if (roomEntity.isPresent()) {
            // RoomEntity 가져오기
            RoomEntity roomEntitys = roomEntity.get();

            // roomSeason 값 업데이트
            roomEntitys.setRoomSeason(roomSeason);

            // 변경된 값을 DB에 저장
            roomRepository.save(roomEntitys);

            log.info("Room(idx=" + idx + ")의 roomSeason이 " + roomSeason + "으로 업데이트되었습니다.");
        } else {
            // 해당 Room이 존재하지 않으면 예외 처리
            throw new IllegalStateException("해당 Room이 존재하지 않습니다.");
        }
    }
}
