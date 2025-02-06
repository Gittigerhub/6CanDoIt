package com.sixcandoit.roomservice.service;

import com.sixcandoit.roomservice.dto.AdvertisementDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.entity.AdvertisementEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.repository.AdvertisementRepository;
import com.sixcandoit.roomservice.repository.office.OrganizationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AdvertisementService {

    // final 선언, 모델 맵퍼 선언
    private final AdvertisementRepository advertisementRepository;
    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;

    // 광고 등록
    public void adRegister(AdvertisementDTO advertisementDTO){

        try {
            // DTO -> Entity로 변환
            AdvertisementEntity advertisementEntity = modelMapper.map(advertisementDTO, AdvertisementEntity.class);

            // 조직 ID로 조회하여 데이터 가져오기
            OrganizationEntity organization = organizationRepository.findById(advertisementDTO.getOrganization().getIdx())
                    .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));

            // 광고테이블에 연관관계 조직테이블 추가
            advertisementEntity.setOrganizationJoin(organization);

            // Entity 테이블에 저장
            advertisementRepository.save(advertisementEntity);
        } catch (Exception e) {     // 오류발생시 오류 처리
            throw new RuntimeException("등록 실패" + e.getMessage());
        }

    }

    // 광고 수정
    public void adUpdate(AdvertisementDTO advertisementDTO){

        // idx로 수정할 데이터 조회
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findById(advertisementDTO.getIdx());

        if (advertisementEntity.isPresent()){               // advertisementEntity가 존재하면

            log.info("수정 진행");
            // DTO -> Entity로 변환
            AdvertisementEntity advertisement = modelMapper.map(advertisementDTO, AdvertisementEntity.class);

            // 조직 ID로 조회하여 데이터 가져오기
            OrganizationEntity organization = organizationRepository.findById(advertisementDTO.getOrganization().getIdx())
                    .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));

            // 광고테이블에 연관관계 조직테이블 데이터 추가
            advertisement.setOrganizationJoin(organization);

            // Entity 테이블에 저장
            advertisementRepository.save(advertisement);

        } else {                                            // advertisementEntity가 존재하지 않으면
            throw new IllegalStateException("수정할 데이터가 존재하지 않습니다.");
        }

    }

    // 광고 삭제
    public void adDelete(Integer idx){

        // idx로 조회하여 삭제
        advertisementRepository.deleteById(idx);

    }

    // 광고 목록
    public Page<AdvertisementDTO> adList(Pageable page, String type, String keyword){

        // 1. 페이지정보를 재가공
        int currentPage = page.getPageNumber()-1;       // 화면의 페이지 번호를 db 페이지 번호로
        int pageLimit = 5;                              // 한 페이지를 구성하는 레코드 수

        // 지정된 내용으로 페이지 정보를 재생산, 정렬 내림차순 DESC
        Pageable pageable = PageRequest.of(currentPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "idx"));

        // 2. 조회
        // type : 전체(0), 제목(1), 조직명(2)
        Page<AdvertisementEntity> advertisementEntities;
        if (keyword != null && !keyword.isEmpty()){         // 검색어가 존재하면
            log.info("검색어가 존재하면...");
            if (type.equals("1")){                          // type 분류 1, 제목으로 검색할 때
                log.info("제목으로 검색을 하는 중...");
                advertisementEntities = advertisementRepository.searchTitle(keyword, page);
            } else if (type.equals("2")){                   // type 분류 2, 조직명으로 검색할 때
                log.info("내용으로 검색을 하는 중...");
                advertisementEntities = advertisementRepository.searchOrgan(keyword, page);
            } else {                                        // type 분류 0, 전체(제목 + 조직명)으로 검색할 때
                log.info("모든 대상으로 검색을 하는 중...");
                advertisementEntities = advertisementRepository.searchAll(keyword, page);
            }
        } else {                                            // 검색어가 존재하지 않으면 모두 검색
            advertisementEntities = advertisementRepository.findAll(pageable);
        }

        // Entity를 DTO로 변환 후 저장
        Page<AdvertisementDTO> advertisementDTOS = advertisementEntities.map(
                data -> modelMapper.map(data, AdvertisementDTO.class));

        return advertisementDTOS;
    }

    // 업체 조회
    public List<OrganizationDTO> searchOrgan(String searchType, String searchWord){

        // 1. 조회
        List<OrganizationEntity> organizationEntities;

        // 여러개를 조회해야 할땐 if문으로 분류따라 조회해야함
        if (searchWord == null) {                         // 타입만 존재한다면
            organizationEntities = organizationRepository.findByOrganType(searchType);
        } else {                                          // 검색어와 타입이 존재한다면
            organizationEntities = organizationRepository.findByOrganTypeAndOrganNameLikeIgnoreCase(searchType, searchWord);
        }

        // 2. Entity를 DTO로 변환 후 저장
        List<OrganizationDTO> organizationDTOS = organizationEntities.stream()
                .map( data -> modelMapper.map( data, OrganizationDTO.class) )
                .collect(Collectors.toList());

        // 3. 조회 값 반환
        return organizationDTOS;
    }

    // 광고 상세보기
    public AdvertisementDTO adRead(Integer idx){

        // idx로 데이터 조회
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findById(idx);
        log.info("게시글의 idx를 조회...");

        // Entity -> DTO
        AdvertisementDTO advertisementDTO = modelMapper.map(advertisementEntity, AdvertisementDTO.class);
        log.info("DTO로 변환하는 중...");

        // 반환
        return advertisementDTO;
    }

}