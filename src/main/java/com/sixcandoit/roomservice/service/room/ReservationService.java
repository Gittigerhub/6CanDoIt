package com.sixcandoit.roomservice.service.room;

import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.entity.room.ReservationEntity;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.room.ReservationRepository;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public List<RoomEntity> getAvailableRooms(Integer organ_idx, LocalDate startDate, LocalDate endDate) {
        log.info("Finding available rooms for organization {}, from {} to {}", organ_idx, startDate, endDate);
        // 예약 날짜와 겹치지 않는 객실 조회 (organization_idx 기준)
        return reservationRepository.findAvailableRoomsByOrganization(organ_idx, startDate, endDate);
    }

    //주어진 ID에 해당하는 데이터를 삭제
    public void reserveDelete(Integer idx) {
        //작업은 Repository
        reservationRepository.deleteById(idx);
    }

    //주어진 DTO를 Entity로 변환한 후, 데이터베이스에 저장하고 저장된 결과를 다시 DTO로 변환하여 반환
    public ReservationDTO reserveInsert(ReservationDTO reservationDTO) {
        log.info("Inserting new reservation: {}", reservationDTO);
        // RoomEntity를 roomIdx로 조회
        Optional<RoomEntity> roomEntityOpt = roomRepository.findById(reservationDTO.getRoomIdx());

        if (roomEntityOpt.isPresent()) {
            log.info("Room found, checking availability...");
            // 예약 기간이 겹치는지 확인
            if (!checkAvailability(reservationDTO.getRoomIdx(), reservationDTO.getStartDate(), reservationDTO.getEndDate())) {
                log.info("Room is not available for the selected dates");
                throw new RuntimeException("선택한 기간에 이미 예약이 존재합니다.");
            }

            ReservationEntity reserveEntity = new ReservationEntity();
            
            // 기본 정보 설정
            reserveEntity.setStartDate(reservationDTO.getStartDate());
            reserveEntity.setEndDate(reservationDTO.getEndDate());
            reserveEntity.setResStatus("1");  // 예약 중으로 상태 변경

            // RoomEntity 설정
            RoomEntity roomEntity = roomEntityOpt.get();
            reserveEntity.setRoomJoin(roomEntity);

            // 회원 정보 설정
            if (reservationDTO.getMemberDTO() != null) {
                MemberEntity memberEntity = modelMapper.map(reservationDTO.getMemberDTO(), MemberEntity.class);
                reserveEntity.setMemberJoin(memberEntity);
                // 회원 이름 설정
                reserveEntity.setUsername(memberEntity.getMemberName());
                log.info("Member information set: {}", memberEntity.getMemberName());
            } else {
                log.warn("No member information provided in reservationDTO");
            }

            // ReservationEntity 저장
            ReservationEntity result = reservationRepository.save(reserveEntity);
            log.info("Reservation saved successfully");
            return modelMapper.map(result, ReservationDTO.class);
        }
        log.warn("Room not found with idx: {}", reservationDTO.getRoomIdx());
        return null;
    }

    //주어진 DTO의 ID로 데이터베이스에서 해당 데이터를 찾아 수정
    public ReservationDTO reserveUpdate(ReservationDTO reservationDTO) {
        log.info("Updating reservation: {}", reservationDTO);
        Optional<ReservationEntity> findData = reservationRepository.findByIdx(reservationDTO.getIdx());

        if(findData.isPresent()) {
            ReservationEntity existingReservation = findData.get();
            
            // 기존 예약과 동일한 roomIdx가 아닌 경우에만 중복 체크
            if (!existingReservation.getRoomJoin().getIdx().equals(reservationDTO.getRoomIdx())) {
                // 예약 기간이 겹치는지 확인
                if (!checkAvailability(reservationDTO.getRoomIdx(), reservationDTO.getStartDate(), reservationDTO.getEndDate())) {
                    throw new RuntimeException("선택한 기간에 이미 예약이 존재합니다.");
                }
            }

            // 기존 예약 엔티티 업데이트
            existingReservation.setStartDate(reservationDTO.getStartDate());
            existingReservation.setEndDate(reservationDTO.getEndDate());
            
            // RoomEntity 설정
            Optional<RoomEntity> roomEntityOpt = roomRepository.findByIdx(reservationDTO.getRoomIdx());
            if (roomEntityOpt.isPresent()) {
                RoomEntity roomEntity = roomEntityOpt.get();
                existingReservation.setRoomJoin(roomEntity);
            }

            // 예약 상태 설정
            existingReservation.setResStatus("1"); // 예약 중으로 상태 변경

            // 기존 회원 정보 유지
            MemberEntity existingMember = existingReservation.getMemberJoin();
            if (existingMember != null) {
                log.info("Keeping existing member information: {}", existingMember.getMemberName());
                // 기존 회원 정보와 이름 유지
                existingReservation.setMemberJoin(existingMember);
                existingReservation.setUsername(existingMember.getMemberName());
            } else {
                log.warn("No existing member information found");
            }

            // 저장 및 반환
            ReservationEntity result = reservationRepository.save(existingReservation);
            log.info("Successfully updated reservation with member info: {}", result.getUsername());
            return modelMapper.map(result, ReservationDTO.class);
        }

        log.warn("Reservation not found with idx: {}", reservationDTO.getIdx());
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
            // 가격 내림차순으로 정렬하여 조회
            reserveEntities = reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "roomJoin.roomPrice"));
        } else {
            if(endDate == null) { //종료날짜가 없으면 시작날짜 다음날로 지정
                endDate = startDate.plusDays(1);
            }
            // 가격 내림차순으로 정렬하여 조회
            reserveEntities = reservationRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqual(
                startDate, endDate, Sort.by(Sort.Direction.DESC, "roomJoin.roomPrice"));
        }

        //데이터값 변환(ModelMapper DTO<->Entity), Page에 대한 변환X
        List<ReservationDTO> reservationDTOS = Arrays.asList(modelMapper.map(reserveEntities, ReservationDTO[].class));

        return reservationDTOS;
    }

    public List<ReservationDTO> reserveListByOrganization(Integer organ_idx, LocalDate startDate, LocalDate endDate) {
        List<ReservationEntity> reserveEntities;

        if(startDate == null) {
            // organ_idx에 해당하는 room들의 예약만 조회
            reserveEntities = reservationRepository.findByRoomJoin_OrganizationJoin_Idx(organ_idx);
        } else {
            if(endDate == null) {
                endDate = startDate.plusDays(1);
            }
            // organ_idx와 날짜 조건에 맞는 예약 조회
            reserveEntities = reservationRepository.findByRoomJoin_OrganizationJoin_IdxAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                organ_idx, startDate, endDate);
        }

        return Arrays.asList(modelMapper.map(reserveEntities, ReservationDTO[].class));
    }

    // 사용자의 예약 목록 조회
    public List<ReservationDTO> getUserReservations(String memberEmail) {
        log.info("Fetching reservations for user: {}", memberEmail);
        try {
            List<ReservationEntity> reservations = reservationRepository.findByMemberEmail(memberEmail);
            log.info("Found {} reservations", reservations.size());
            return Arrays.asList(modelMapper.map(reservations, ReservationDTO[].class));
        } catch (Exception e) {
            log.error("Error fetching reservations for user: {}", memberEmail, e);
            return new ArrayList<>();
        }
    }

    // 사용자의 특정 숙소 예약 목록 조회
    public List<ReservationDTO> getUserReservationsByOrganization(String memberEmail, Integer organ_idx) {
        log.info("Fetching reservations for user: {} and organization: {}", memberEmail, organ_idx);
        try {
            List<ReservationEntity> reservations = reservationRepository.findByMemberEmailAndOrganization(memberEmail, organ_idx);
            log.info("Found {} reservations", reservations.size());
            // 각 예약의 상세 정보 로깅
            for (ReservationEntity reservation : reservations) {
                log.info("Reservation: id={}, organization={}, room={}, dates={}-{}", 
                    reservation.getIdx(),
                    reservation.getRoomJoin().getOrganizationJoin().getIdx(),
                    reservation.getRoomJoin().getRoomName(),
                    reservation.getStartDate(),
                    reservation.getEndDate());
            }
            return Arrays.asList(modelMapper.map(reservations, ReservationDTO[].class));
        } catch (Exception e) {
            log.error("Error fetching reservations for user: {} and organization: {}", memberEmail, organ_idx, e);
            return new ArrayList<>();
        }
    }
}
