package com.sixcandoit.roomservice.service.room;

import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.entity.room.ReservationEntity;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.room.ReservationRepository;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    // 예약이 가능한지 확인
    private boolean checkAvailability(Integer roomIdx, LocalDate startDate, LocalDate endDate) {
        List<ReservationEntity> overlappingReservations = reservationRepository.findOverlappingReservations(roomIdx, startDate, endDate);
        return overlappingReservations.isEmpty();
    }

    // 빈 객실 조회
    public List<RoomEntity> getAvailableRooms(LocalDate startDate, LocalDate endDate) {
        // 예약 날짜와 겹치지 않는 객실 조회
        return reservationRepository.findAvailableRooms(startDate, endDate);
    }

    //주어진 ID에 해당하는 데이터를 삭제
    public void reserveDelete(Integer idx) {
        //작업은 Repository
        reservationRepository.deleteById(idx);
    }

    //주어진 DTO를 Entity로 변환한 후, 데이터베이스에 저장하고 저장된 결과를 다시 DTO로 변환하여 반환
    public ReservationDTO reserveInsert(ReservationDTO reservationDTO) {
        // RoomEntity를 roomIdx로 조회
        Optional<RoomEntity> roomEntityOpt = roomRepository.findByIdx(reservationDTO.getRoomIdx());

        if (roomEntityOpt.isPresent()) {
            log.info("방이 존재하면...");
            ReservationEntity reserveEntity = modelMapper
                    .map(reservationDTO, ReservationEntity.class);

            log.info("예약 기간이 겹치면...");
            // 예약 기간이 겹치는지 확인
            if (!checkAvailability(reservationDTO.getRoomIdx(), reservationDTO.getStartDate(), reservationDTO.getEndDate())) {
                log.info("예약 기간 겹침......저장x");
                throw new RuntimeException("선택한 기간에 이미 예약이 존재합니다.");
            }
            log.info("예약 기간이 겹치치 않음...");
            // RoomEntity 설정
            RoomEntity roomEntity = roomEntityOpt.get();
            reserveEntity.setRoomJoin(roomEntity);  // ReservationEntity에 RoomEntity 설정

            // 예약 상태 변경
            reserveEntity.setResStatus("1");  // 예약 중으로 상태 변경

            // ReservationEntity 저장
            ReservationEntity result = reservationRepository.save(reserveEntity);
            ReservationDTO resultDTO = modelMapper.map(result, ReservationDTO.class);

            return resultDTO;
        }
        // 방 정보가 없으면 null 반환
        return null;
    }

    //주어진 DTO의 ID로 데이터베이스에서 해당 데이터를 찾아 수정
    public ReservationDTO reserveUpdate(ReservationDTO reservationDTO) {
        Optional<ReservationEntity> findData =
                reservationRepository.findByIdx(reservationDTO.getIdx());

        if(findData.isPresent()) {
            // 기존 ReservationEntity를 업데이트할 때, RoomEntity 상태도 고려
            ReservationEntity reserveEntity = modelMapper.map(reservationDTO, ReservationEntity.class);

            // 예약 기간이 겹치는지 확인
            if (!checkAvailability(reservationDTO.getRoomIdx(), reservationDTO.getStartDate(), reservationDTO.getEndDate())) {
                throw new RuntimeException("선택한 기간에 이미 예약이 존재합니다.");
            }

            // RoomEntity를 roomIdx로 조회
            Optional<RoomEntity> roomEntityOpt = roomRepository.findByIdx(reservationDTO.getRoomIdx());

            if (roomEntityOpt.isPresent()) {
                RoomEntity roomEntity = roomEntityOpt.get();
                reserveEntity.setRoomJoin(roomEntity);  // ReservationEntity에 RoomEntity 설정
            }
            reserveEntity.setResStatus("1"); // 예약 중으로 상태 변경

            // ReservationEntity 저장
            ReservationEntity result = reservationRepository.save(reserveEntity);
            ReservationDTO resultDTO = modelMapper.map(result, ReservationDTO.class);

            return resultDTO;
        }

        return null;
    }

    //주어진 ID에 해당하는 개별 데이터를 조회하여 DTO로 반환
    public ReservationDTO reserveRead(Integer idx) {
        Optional<ReservationEntity> reserveEntity = reservationRepository.findByIdx(idx);

        ReservationDTO reservationDTO = modelMapper.map(reserveEntity, ReservationDTO.class);
        return reservationDTO;
    }

    public List<ReservationDTO> reserveList(LocalDate startDate, LocalDate endDate) {
        List<ReservationEntity> reserveEntities;

        if(startDate == null) { //시작날짜가 없으면 전체 조회
            reserveEntities = reservationRepository.findAll();

        } else {
            if(endDate == null) { //종료날짜가 없으면 시작날짜 다음날로 지정
                endDate = startDate.plusDays(1);
            }
            reserveEntities = reservationRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate);
        }

        //데이터값 변환(ModelMapper DTO<->Entity), Page에 대한 변환X
        List<ReservationDTO> reservationDTOS = Arrays.asList(modelMapper.map(reserveEntities, ReservationDTO[].class));

        return reservationDTOS;
    }
}
