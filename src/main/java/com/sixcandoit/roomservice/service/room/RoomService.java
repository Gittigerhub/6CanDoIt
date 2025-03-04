package com.sixcandoit.roomservice.service.room;

import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.room.ReservationRepository;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private ReservationRepository reservationRepository;
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

    // 예약 관리 - 룸 목록
    public List<RoomDTO> resList(){
        List<RoomEntity> roomEntities = roomRepository.findAll(); // 모두 조회
        List<RoomDTO> roomDTOS = Arrays.asList(modelMapper.map(roomEntities, RoomDTO[].class));
        return roomDTOS;
    }

    // 객실 관리 - 룸 목록
    public Page<RoomDTO> roomList(Pageable page, String type, String keyword, String order){

        int currentPage = page.getPageNumber()-1; // 화면의 페이지 번호를 db 페이지 번호로
        int pageLimit = 5; // 한 페이지를 구성하는 레코드 수

        // 지정된 내용으로 페이지 정보를 재생산, 정렬 내림차순 DESC
        Pageable pageable;

        // 가격 순서 (ASC/DESC)에 따라 정렬 처리
        if ("ASC".equals(order)) {
            pageable = PageRequest.of(currentPage, pageLimit, Sort.by(Sort.Direction.ASC, "roomPrice")); // 가격 올림차순
        } else if ("DESC".equals(order)) {
            pageable = PageRequest.of(currentPage, pageLimit, Sort.by(Sort.Direction.DESC, "roomPrice")); // 가격 내림차순
        } else {
            pageable = PageRequest.of(currentPage, pageLimit, Sort.by(Sort.Direction.DESC, "idx")); // 기본 정렬
        }

        // 조회한 변수를 선언
        // type : 룸 타입(1), 룸 이름(2), 조식 있음(3), 전체(0)
        Page<RoomEntity> roomEntities;

        if (type.equals("3")) { // type 분류 3, 조식 있음만 출력
            log.info("조식 있음만 출력하는 중...");
            roomEntities = roomRepository.searchRoomBreakfast(pageable);
        } else if (type.equals("4")) {
            log.info("빈 방만 출력하는 중...");
            roomEntities = roomRepository.searchRes1(pageable);
        } else if (type.equals("5")) {
            log.info("예약중만 출력하는 중...");
            roomEntities = roomRepository.searchRes2(pageable);
        } else if (type.equals("6")) {
            log.info("체크인만 출력하는 중...");
            roomEntities = roomRepository.searchRes3(pageable);
        } else if (type.equals("7")) {
            log.info("체크아웃만 출력하는 중...");
            roomEntities = roomRepository.searchRes4(pageable);
        } else {
            // keyword가 존재하는 경우 검색
            if (keyword != null && !keyword.isEmpty()) { // 검색어가 존재하면
                log.info("검색어가 존재하면...");
                if (type.equals("1")) { // type 분류 1, 타입으로 검색할 때
                    log.info("타입으로 검색을 하는 중...");
                    roomEntities = roomRepository.searchRoomType(keyword, pageable);
                } else if (type.equals("2")) { // type 분류 2, 이름으로 검색할 때
                    log.info("이름으로 검색을 하는 중...");
                    roomEntities = roomRepository.searchRoomName(keyword, pageable);
                } else { // 전체 검색 = 0
                    log.info("전체 조회 검색 중...");
                    roomEntities = roomRepository.findAll(pageable);
                }
            } else { // 검색어가 존재하지 않으면 모두 검색
                log.info("검색어가 존재하지 않으면 모두 검색...");
                roomEntities = roomRepository.findAll(pageable);
            }
        }

        // Entity를 DTO로 변환 후 저장
        Page<RoomDTO> roomDTOS = roomEntities.map(
                data->modelMapper.map(data, RoomDTO.class));

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
