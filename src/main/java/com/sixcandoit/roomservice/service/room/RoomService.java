package com.sixcandoit.roomservice.service.room;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.office.OrganizationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class RoomService {

    // final 선언, 모델 맵퍼 선언
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final ImageFileService imageFileService;
    private final OrganizationService organizationService;

    // 룸 등록
    public void roomRegister(RoomDTO roomDTO, List<MultipartFile> imageFiles){
        System.out.println(imageFiles);
        try {
            // DTO로 Entity 변환
            RoomEntity room = modelMapper.map(roomDTO, RoomEntity.class);
            
            // organ_idx가 있다면 OrganizationEntity 설정
            if (roomDTO.getOrgan_idx() != null) {
                OrganizationEntity organization = organizationService.read(roomDTO.getOrgan_idx())
                        .orElseThrow(() -> new RuntimeException("Organization not found with id: " + roomDTO.getOrgan_idx()));
                room.setOrganizationJoin(organization);
            }
            
            // 룸 기본 값:
            room.setRoomView("N"); // 창문 없음
            room.setRoomSeason("off"); // 비성수기
            room.setRoomCondition("Y"); // 청소 완료
            room.setResStatus("1"); // 예약 상태(1:빈 방, 2:예약, 3:체크인, 4:체크 아웃)

            //--------------------------------------------------------------//
            // 이미지 등록
            log.info("이미지를 저장한다...");
            List<ImageFileEntity> images = imageFileService.saveImages(imageFiles);

            // 이미지 정보 추가
            for (ImageFileEntity image : images) {
                room.addImage(image);
            }
            //--------------------------------------------------------------//

            // 저장
            roomRepository.save(room);
        } catch (Exception e){
            throw new RuntimeException("이미지 저장 실패 : "+e.getMessage());
        }
    }

    // 룸 수정
    public void roomUpdate(RoomDTO roomDTO, String join, List<MultipartFile> imageFiles){
        try {
            System.out.println("이미지 파일즈 길이 : " + imageFiles.size());// 룸 데이터의 idx를 조회
            log.info("룸 데이터의 idx를 조회한다...");
            log.info("수정할 룸의 idx: " + roomDTO.getIdx());
            Optional<RoomEntity> roomEntityOpt = roomRepository.findByIdx(roomDTO.getIdx());

            if (roomEntityOpt.isPresent()) { // RoomEntity가 존재하면
                RoomEntity room = roomEntityOpt.get();

                // 기존 RoomEntity에서 ResStatus를 그대로 유지
                room.setRoomName(roomDTO.getRoomName());
                room.setRoomInfo(roomDTO.getRoomInfo());
                room.setRoomType(roomDTO.getRoomType());
                room.setRoomView(roomDTO.getRoomView());
                room.setRoomNum(roomDTO.getRoomNum());
                room.setRoomBed(roomDTO.getRoomBed());
                room.setRoomPrice(roomDTO.getRoomPrice());
                room.setRoomSize(roomDTO.getRoomSize());
                room.setRoomBreakfast(roomDTO.getRoomBreakfast());
                room.setRoomSmokingYn(roomDTO.getRoomSmokingYn());
                room.setRoomWifi(roomDTO.getRoomWifi());
                room.setRoomTv(roomDTO.getRoomTv());
                room.setRoomAir(roomDTO.getRoomAir());
                room.setRoomBath(roomDTO.getRoomBath());
                room.setRoomCheckIn(roomDTO.getRoomCheckIn());
                room.setRoomCheckOut(roomDTO.getRoomCheckOut());

                //--------------------------------------------------------------//
                // 이미지 추가 등록
                log.info("수정 이미지 추가를 진행");
                List<ImageFileEntity> images = imageFileService.updateImage(imageFiles, join, roomDTO.getIdx());
                // 이미지 정보 추가
                // 양방향 연관관계 편의 메서드 사용
                if (images != null && !images.isEmpty()) {
                    log.info("새로운 이미지가 등록되었습니다.");
                    for (ImageFileEntity image : images) {
                        room.addImage(image);  // FK 자동 설정
                    }
                } else {
                    log.warning("이미지가 없거나 저장 실패.");
                }
                //--------------------------------------------------------------//

                // 저장
                roomRepository.save(room);

                log.info("룸 데이터가 업데이트되었습니다.");
            } else {
                throw new IllegalStateException("수정할 Room이 존재하지 않습니다.");
            }
        }catch (Exception e){
            log.severe("수정 오류 발생: " + e.getMessage());
            throw new RuntimeException("수정 오류 발생: " + e.getMessage());
        }
    }

    // 룸 삭제
    public void roomDelete(Integer idx, String join){
        try {
            // 이미지 조회
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(idx, join);

            // dto => entity
            List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                    .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                    .collect(Collectors.toList());

            // 모든 이미지 삭제
            for (ImageFileEntity imageFileEntity : imageFileEntities) {
                imageFileService.deleteImage(imageFileEntity.getIdx());
            }

            roomRepository.deleteById(idx);
        } catch (Exception e) {
            throw new RuntimeException("삭제를 실패했습니다." + e.getMessage());
        }

    }

    // 예약 관리 - 룸 목록
    public List<RoomDTO> resList(){
        List<RoomEntity> roomEntities = roomRepository.findAll(); // 모두 조회
        return roomEntities.stream()
                .map(entity -> {
                    RoomDTO dto = modelMapper.map(entity, RoomDTO.class);
                    if (entity.getOrganizationJoin() != null) {
                        dto.setOrgan_idx(entity.getOrganizationJoin().getIdx());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
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

    // 특정 organization의 룸 목록 조회
    public Page<RoomDTO> getRoomsByOrganization(Integer organIdx, Pageable page) {
        int currentPage = page.getPageNumber()-1;
        int pageLimit = 5;
        
        Pageable pageable = PageRequest.of(currentPage, pageLimit, Sort.by(Sort.Direction.DESC, "idx"));
        
        Page<RoomEntity> roomEntities = roomRepository.findByOrganIdx(organIdx, pageable);
        return roomEntities.map(entity -> modelMapper.map(entity, RoomDTO.class));
    }

    // 사용자용 organization별 룸 목록 조회
    public List<RoomDTO> getRoomsByOrganizationForMember(Integer organIdx) {
        // 가격 내림차순으로 정렬
        Sort sort = Sort.by(Sort.Direction.DESC, "roomPrice");
        List<RoomEntity> roomEntities = roomRepository.findByOrganizationJoin_IdxOrderByRoomPriceDesc(organIdx);
        
        return roomEntities.stream()
                .map(entity -> {
                    RoomDTO dto = modelMapper.map(entity, RoomDTO.class);
                    if (entity.getOrganizationJoin() != null) {
                        dto.setOrgan_idx(entity.getOrganizationJoin().getIdx());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
