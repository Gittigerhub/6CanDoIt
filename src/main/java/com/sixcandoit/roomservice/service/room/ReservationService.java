package com.sixcandoit.roomservice.service.room;

import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.entity.room.ReservationEntity;
import com.sixcandoit.roomservice.repository.room.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;

    //주어진 ID에 해당하는 데이터를 삭제
    public void reserveDelete(Integer id) {
        //작업은 Repository
        reservationRepository.deleteById(id);
    }

    //주어진 DTO를 Entity로 변환한 후, 데이터베이스에 저장하고 저장된 결과를 다시 DTO로 변환하여 반환
    public ReservationDTO reserveInsert(ReservationDTO reservationDTO) {
        ReservationEntity reserveEntity = modelMapper
                .map(reservationDTO, ReservationEntity.class);
        ReservationEntity result = reservationRepository.save(reserveEntity); //필요한 값은 Entity

        ReservationDTO resultDTO = modelMapper.map(result, ReservationDTO.class);

        return resultDTO;
    }

    //주어진 DTO의 ID로 데이터베이스에서 해당 데이터를 찾아 수정
    public ReservationDTO reserveUpdate(ReservationDTO reservationDTO) {
        Optional<ReservationEntity> findData =
                reservationRepository.findById(reservationDTO.getIdx());

        if(findData.isPresent()) {
            ReservationEntity reserveEntity = modelMapper.map(reservationDTO, ReservationEntity.class);
            ReservationEntity result = reservationRepository.save(reserveEntity);
            ReservationDTO resultDTO = modelMapper.map(result, ReservationDTO.class);

            return resultDTO;
        }

        return null;
    }

    //주어진 ID에 해당하는 개별 데이터를 조회하여 DTO로 반환
    public ReservationDTO reserveRead(Integer id) {
        Optional<ReservationEntity> reserveEntity = reservationRepository.findById(id);

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
