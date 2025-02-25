package com.sixcandoit.roomservice.service;

import com.sixcandoit.roomservice.dto.AdvertisementDTO;
import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.entity.AdvertisementEntity;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final ImageFileService imageFileService;

    /* -----------------------------------------------------------------------------
        함수명 : void adRegister(AdvertisementDTO advertisementDTO, Integer idx)
        인수 : AdvertisementDTO advertisementDTO, Integer idx
        출력 : 없음
        설명 : 전달받은 데이터를 데이터베이스에 저장하여 광고 등록
    ----------------------------------------------------------------------------- */
    public void adRegister(AdvertisementDTO advertisementDTO, Integer organidx, List<MultipartFile> imageFiles) {

        try {
            // DTO -> Entity로 변환
            AdvertisementEntity advertisementEntity = modelMapper.map(advertisementDTO, AdvertisementEntity.class);

            // 조직 ID로 조회하여 데이터 가져오기
            OrganizationEntity organization = organizationRepository.findById(organidx)
                    .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));

            // 광고테이블에 연관관계 조직테이블 추가
            advertisementEntity.setOrganizationJoin(organization);

            // 이미지 등록
            List<ImageFileEntity> images = imageFileService.saveImages(imageFiles);

            // 이미지 정보 추가
            // 양방향 연관관계 편의 메서드 사용
            for (ImageFileEntity image : images) {
                advertisementEntity.addImage(image);  // FK 자동 설정
            }
            System.out.println("FK 자동 등록");

            // Entity 테이블에 저장
            advertisementRepository.save(advertisementEntity);

        } catch (Exception e) {     // 오류발생시 오류 처리
            throw new RuntimeException("등록 실패" + e.getMessage());
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : void adUpdate(AdvertisementDTO advertisementDTO, String join, List<MultipartFile> imageFiles)
        인수 : (AdvertisementDTO advertisementDTO, String join, List<MultipartFile> imageFiles
        출력 : 없음
        설명 : 전달받은 데이터를 데이터베이스에서 조회하여 수정
    ----------------------------------------------------------------------------- */
    public void adUpdate(AdvertisementDTO advertisementDTO, String join, List<MultipartFile> imageFiles) {

        try {
            // idx로 수정할 데이터 조회
            Optional<AdvertisementEntity> advertisementEntity =
                    advertisementRepository.findById(advertisementDTO.getIdx());

            if(!advertisementEntity.isPresent()){       // 조회 값이 없다면
                throw new RuntimeException("수정할 광고 조회 실패");
            }
            else {                                      // advertisementEntity가 존재하면
                // DTO -> Entity로 변환
                AdvertisementEntity advertisement =
                        modelMapper.map(advertisementDTO, AdvertisementEntity.class);

                // 조직 ID로 조회하여 데이터 가져오기
                OrganizationEntity organization = organizationRepository.findById(advertisementDTO.getOrganizationJoin().getIdx())
                        .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));

                // 광고테이블에 연관관계 조직테이블 데이터 추가
                advertisement.setOrganizationJoin(organization);

                // 이미지 추가 등록
                List<ImageFileEntity> images =
                        imageFileService.updateImage(imageFiles, join, advertisementDTO.getIdx());

                // 이미지 정보 추가
                // 양방향 연관관계 편의 메서드 사용
                for (ImageFileEntity image : images) {
                    advertisement.addImage(image);  // FK 자동 설정
                }

                // Entity 테이블에 저장
                advertisementRepository.save(advertisement);
            }

        } catch (Exception e) {             // 오류발생시 오류 처리
            throw new RuntimeException("수정서비스 실패" + e.getMessage());
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : void adDelete(Integer idx, String join)
        인수 : Integer idx, String join
        출력 : 없음
        설명 : 전달받은 데이터를 데이터베이스에서 조회하여 삭제
    ----------------------------------------------------------------------------- */
    public void adDelete(Integer idx, String join) {

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

            // idx로 조회하여 삭제
            advertisementRepository.deleteById(idx);

        } catch (Exception e) {
            throw new RuntimeException("삭제를 실패했습니다." + e.getMessage());
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : Page<AdvertisementDTO> adList(Pageable page, String type, String keyword)
        인수 : Pageable page, String type, String keyword
        출력 : 타입과 키워드로 검색된 데이터의 리스트 출력
        설명 : 전달받은 데이터를 데이터베이스에서 조회하여 리스트 출력
    ----------------------------------------------------------------------------- */
    public Page<AdvertisementDTO> adList(Pageable page, String type, String keyword) {

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

    /* -----------------------------------------------------------------------------
        함수명 : List<OrganizationDTO> searchOrgan(String searchType, String searchWord)
        인수 : String searchType, String searchWord
        출력 : 타입과 키워드로 검색된 데이터의 리스트 출력
        설명 : 전달받은 데이터를 데이터베이스에서 조회하여 업체 리스트 출력
    ----------------------------------------------------------------------------- */
    public List<OrganizationDTO> searchOrgan(String searchType, String searchWord) {

        // 1. 조회
        List<OrganizationEntity> organizationEntities;

        // 여러개를 조회해야 할땐 if문으로 분류따라 조회해야함
        if (searchWord == null && searchWord.trim().isEmpty()) {                         // 타입만 존재한다면
            organizationEntities = organizationRepository.findByOrganType(searchType);
        } else {                                          // 검색어와 타입이 존재한다면
            organizationEntities = organizationRepository.findByOrganTypeAndOrganNameLikeIgnoreCase(searchType, "%" + searchWord + "%");
        }

        // 2. Entity를 DTO로 변환 후 저장
        List<OrganizationDTO> organizationDTOS = organizationEntities.stream()
                .map( data -> modelMapper.map( data, OrganizationDTO.class) )
                .collect(Collectors.toList());

        // 3. 조회 값 반환
        return organizationDTOS;
    }

    /* -----------------------------------------------------------------------------
        함수명 : AdvertisementDTO adRead(Integer idx)
        인수 : Integer idx
        출력 : 뷰 페이지로 데이터 전달
        설명 : 전달받은 데이터를 데이터베이스에서 조회하여 출력
    ----------------------------------------------------------------------------- */
    public AdvertisementDTO adRead(Integer idx) {

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