package com.sixcandoit.roomservice.service.office;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.office.OrganizationRepository;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;
    private final ImageFileService imageFileService;
    private final RoomRepository roomRepository;

    /* -----------------------------------------------------------------------------
        함수명 : void organRegister(OrganizationDTO organizationDTO)
        인수 : 입력 폼으로 부터 받는 OrganizationDTO
        출력 : 없음
        설명 : 전달받은 데이터를 데이터베이스에 저장하여 조직 등록
    ----------------------------------------------------------------------------- */
    public void organRegister(OrganizationDTO organizationDTO, Integer findIdx, String hoORshop, List<MultipartFile> imageFiles) {

        System.out.println(findIdx);
        System.out.println(hoORshop);

        try {
            // DTO -> Entity로 변환
            OrganizationEntity organ = modelMapper.map(organizationDTO, OrganizationEntity.class);

            // ActiveYn의 최초 기본값은 "Y"
            organ.setActiveYn("Y");

            // 이미지 등록
            List<ImageFileEntity> images = imageFileService.saveImages(imageFiles);

            // 이미지 정보 추가
            // 양방향 연관관계 편의 메서드 사용
            for (ImageFileEntity image : images) {
                organ.addImage(image);  // FK 자동 설정
            }
            System.out.println("FK 자동 등록");

            if (hoORshop.trim().equals("head")) {
                // 본사 데이터 조회
                OrganizationEntity hoOrgan = organizationRepository.findById(findIdx).orElseThrow(() -> new RuntimeException("본사를 찾을 수 없습니다."));

                // 본사 FK 연결
                organ.setHead(hoOrgan);

                // Entity 테이블에 저장
                organizationRepository.save(organ);
                System.out.println("head까지 저장 최최최종");
            } else if (hoORshop.trim().equals("hotels")) {
                // 호텔 데이터 조회
                OrganizationEntity hotelsOrgan = organizationRepository.findById(findIdx).orElseThrow(() -> new RuntimeException("호텔을 찾을 수 없습니다."));

                // 호텔 FK 연결
                organ.setHotels(hotelsOrgan);

                // Entity 테이블에 저장
                organizationRepository.save(organ);
                System.out.println("hotels까지 저장 최최최종");
            } else {
                // Entity 테이블에 저장
                organizationRepository.save(organ);
                System.out.println("저장 최최최종");
            }

        } catch (Exception e) {             // 오류발생시 오류 처리
            throw new RuntimeException("등록 실패");
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : void organUpdate(OrganizationDTO organizationDTO)
        인수 : 수정할 데이터(OrganizationDTO)
        출력 : 없음
        설명 : 전달받은 데이터를 데이터베이스에 저장하여 조직 정보 수정
    ----------------------------------------------------------------------------- */
    public void organUpdate(OrganizationDTO organizationDTO, String join, List<MultipartFile> imageFiles) {

        try {
            // idx로 데이터 조회
            OrganizationEntity read =
                    organizationRepository.findById(organizationDTO.getIdx())
                            .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));

            // 이미지 추가 등록
            List<ImageFileEntity> images =
                    imageFileService.updateImage(imageFiles, join, organizationDTO.getIdx());

            // 이미지 정보 추가
            // 양방향 연관관계 편의 메서드 사용
            for (ImageFileEntity image : images) {
                read.addImage(image);  // FK 자동 설정
            }

            // 기존 엔티티에 덮어씌우기
            read.setOrganName(organizationDTO.getOrganName());
            read.setOrganType(organizationDTO.getOrganType());
            read.setOrganAddress(organizationDTO.getOrganAddress());
            read.setOrganTel(organizationDTO.getOrganTel());
            read.setActiveYn(organizationDTO.getActiveYn());

            // Entity 테이블에 저장
            organizationRepository.save(read);

        } catch (Exception e) {             // 오류발생시 오류 처리
            throw new RuntimeException("수정서비스 실패" + e.getMessage());
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : OrganizationDTO organRead(Integer idx)
        인수 : 조회할 조직 idx
        출력 : idx로 조회된 데이터
        설명 : 전달받은 idx값으로 데이터베이스를 조회하여 값 출력
    ----------------------------------------------------------------------------- */
    public OrganizationDTO organRead(Integer idx) {

        try {
            // 데이터베이스 조회
            Optional<OrganizationEntity> organEntity =
                    organizationRepository.findById(idx);

            // Entity -> DTO 변환
            OrganizationDTO organDTO = modelMapper.map(organEntity, OrganizationDTO.class);

            // 반환
            return organDTO;
        } catch (Exception e) {
            throw new RuntimeException("조회 오류");
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : Page<OrganizationDTO> organList()
        인수 : Pageable page, String type, String keyword, Integer adminIdx
        출력 : 받은 인수들로 데이터 조회
        설명 : 관리자의 조직조회
    ----------------------------------------------------------------------------- */
    public Page<OrganizationDTO> organList(Pageable page, String type, String keyword, Integer adminIdx) {

        System.out.println(page);
        System.out.println(type);
        System.out.println(keyword);
        System.out.println(adminIdx);

        try {
            // 1. 페이지정보를 재가공
            int currentPage = page.getPageNumber() - 1;
            int pageSize = 5;

            // 기본 키로 올림차순해서 페이지 조회
            Pageable pageable = PageRequest.of(currentPage, pageSize,
                    Sort.by(Sort.Direction.ASC, "idx"));

            if (adminIdx != null) {

                // 2. 관리자 권환 확인
                OrganizationEntity organ = organizationRepository.findById(adminIdx)
                        .orElseThrow(() -> new RuntimeException("권환 확인을 위한 조직 조회 실패"));

                // 관리자 권한
                String roles = organ.getOrganType();

                // 3. 조회
                // 조회결과를 저장할 변수
                Page<OrganizationEntity> organizationEntities = Page.empty(pageable);

                // 여러개를 조회해야 할땐 if문으로 분류따라 조회해야함
                if (roles.equals("HO")) {
                    if (keyword == null) {                           // 타입만 존재한다면
                        if (type.equals("BO")) {
                            organizationEntities = organizationRepository.searchHOB(pageable, adminIdx);    // 타입에 해당하는 데이터 조회
                        }  else if (type.equals("SHOP")) {
                            organizationEntities = organizationRepository.searchHOS(pageable, adminIdx);    // 타입에 해당하는 데이터 조회
                        } else if (type.equals("ALLHO")) {
                            organizationEntities = organizationRepository.searchHOA(pageable, adminIdx);    // 타입에 해당하는 데이터 조회
                        }
                    } else {                                         // 검색어와 타입이 존재한다면
                        if (type.equals("BO")) {
                            organizationEntities = organizationRepository.searchHOBName(keyword, pageable, adminIdx);     // 검색어에 해당하는 데이터 조회
                        } else if (type.equals("SHOP")) {
                            organizationEntities = organizationRepository.searchHOSName(keyword, pageable, adminIdx);     // 검색어에 해당하는 데이터 조회
                        } else if (type.equals("ALLHO")) {
                            organizationEntities = organizationRepository.searchHOAName(keyword, pageable, adminIdx);     // 검색어에 해당하는 데이터 조회
                        }
                    }
                } else if (roles.equals("BO")) {
                    if (keyword == null) {                           // 타입만 존재한다면
                        if (type.equals("SHOP")) {
                            organizationEntities = organizationRepository.searchBOS(pageable, adminIdx);    // 타입에 해당하는 데이터 조회
                        } else if (type.equals("ALLBO")) {
                            organizationEntities = organizationRepository.searchBOA(pageable, adminIdx);    // 타입에 해당하는 데이터 조회
                        }
                    } else {                                         // 검색어와 타입이 존재한다면
                        if (type.equals("SHOP")) {
                            organizationEntities = organizationRepository.searchBOSName(keyword, pageable, adminIdx);     // 검색어에 해당하는 데이터 조회
                        } else if (type.equals("ALLBO")) {
                            organizationEntities = organizationRepository.searchBOAName(keyword, pageable, adminIdx);     // 검색어에 해당하는 데이터 조회
                        }
                    }
                }

                // 4. 조회한 결과를 HTML에서 사용할 DTO로 변환
                Page<OrganizationDTO> organizationDTOS =
                        organizationEntities.map(entity ->
                                modelMapper.map(entity, OrganizationDTO.class));

                // 5. 결과값을 전달
                return organizationDTOS;

            } else {

                // 3. 조회
                // 조회결과를 저장할 변수
                Page<OrganizationEntity> organizationEntities = Page.empty(pageable);

                // 여러개를 조회해야 할땐 if문으로 분류따라 조회해야함
                if (keyword == null) {                           // 타입만 존재한다면
                    if (type.equals("HO")) {
                        organizationEntities = organizationRepository.searchHO(pageable);     // 타입에 해당하는 데이터 조회
                    } else if (type.equals("BO")) {
                        organizationEntities = organizationRepository.searchBO(pageable);     // 타입에 해당하는 데이터 조회
                    } else if (type.equals("SHOP")) {
                        organizationEntities = organizationRepository.searchSHOP(pageable);   // 타입에 해당하는 데이터 조회
                    } else if (type.equals("ALL")) {
                        organizationEntities = organizationRepository.searchALL(pageable);    // 타입에 해당하는 데이터 조회
                    }
                } else {                                         // 검색어와 타입이 존재한다면
                    if (type.equals("HO")) {
                        organizationEntities = organizationRepository.searchHOName(keyword, pageable);       // 검색어에 해당하는 데이터 조회
                    } else if (type.equals("BO")) {
                        organizationEntities = organizationRepository.searchBOName(keyword, pageable);       // 검색어에 해당하는 데이터 조회
                    } else if (type.equals("SHOP")) {
                        organizationEntities = organizationRepository.searchSHOPName(keyword, pageable);     // 검색어에 해당하는 데이터 조회
                    } else if (type.equals("ALL")) {
                        organizationEntities = organizationRepository.searchALLName(keyword, pageable);      // 검색어에 해당하는 데이터 조회
                    }
                }

                // 4. 조회한 결과를 HTML에서 사용할 DTO로 변환
                Page<OrganizationDTO> organizationDTOS =
                        organizationEntities.map(entity ->
                                modelMapper.map(entity, OrganizationDTO.class));

                // 5. 결과값을 전달
                return organizationDTOS;

            }

        } catch (Exception e) {     //오류발생시 오류 처리
            throw new RuntimeException("조회 오류");
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : Page<OrganizationDTO> organList()
        인수 : Pageable page, String type, String keyword, Integer adminIdx
        출력 : 받은 인수들로 데이터 조회
        설명 : 관리자의 조직조회
    ----------------------------------------------------------------------------- */
    public List<OrganizationDTO> searchOrgan(String searchType, String searchWord, Integer adminIdx) {

        System.out.println(searchType);
        System.out.println(searchWord);
        System.out.println(adminIdx);

        try {

            if (adminIdx != null) {

                // 2. 관리자 권환 확인
                OrganizationEntity organ = organizationRepository.findById(adminIdx)
                        .orElseThrow(() -> new RuntimeException("권환 확인을 위한 조직 조회 실패"));

                // 관리자 권한
                String roles = organ.getOrganType();

                // 3. 조회
                // 조회결과를 저장할 변수
                List<OrganizationEntity> organizationEntities = new ArrayList<>();

                // 여러개를 조회해야 할땐 if문으로 분류따라 조회해야함
                if (roles.equals("HO")) {
                    if (searchWord == null) {                           // 타입만 존재한다면
                        if (searchType.equals("HO")) {
                            organizationEntities = organizationRepository.findHOH(adminIdx);    // 타입에 해당하는 데이터 조회
                        } else if (searchType.equals("BO")) {
                            organizationEntities = organizationRepository.findHOB(adminIdx);    // 타입에 해당하는 데이터 조회
                        }
                    } else {                                         // 검색어와 타입이 존재한다면
                        if (searchType.equals("HO")) {
                            organizationEntities = organizationRepository.findHOHName(searchWord, adminIdx);     // 검색어에 해당하는 데이터 조회
                        } else if (searchType.equals("BO")) {
                            organizationEntities = organizationRepository.findHOBName(searchWord, adminIdx);     // 검색어에 해당하는 데이터 조회
                        }
                    }
                } else if (roles.equals("BO")) {
                    if (searchWord == null) {                           // 타입만 존재한다면
                        if (searchType.equals("BO")) {
                            organizationEntities = organizationRepository.findBOB(adminIdx);    // 타입에 해당하는 데이터 조회
                        }
                    } else {                                         // 검색어와 타입이 존재한다면
                        if (searchType.equals("BO")) {
                            organizationEntities = organizationRepository.findBOBName(searchWord, adminIdx);     // 검색어에 해당하는 데이터 조회
                        }
                    }
                }

                // 4. 조회한 결과를 HTML에서 사용할 DTO로 변환
                List<OrganizationDTO> organizationDTOS = organizationEntities.stream()
                        .map(data -> modelMapper.map(data, OrganizationDTO.class) )
                        .collect(Collectors.toList());

                // 5. 결과값을 전달
                return organizationDTOS;

            } else {

                // 3. 조회
                // 조회결과를 저장할 변수
                List<OrganizationEntity> organizationEntities = new ArrayList<>();

                // 여러개를 조회해야 할땐 if문으로 분류따라 조회해야함
                if (searchWord == null) {                           // 타입만 존재한다면
                    if (searchType.equals("HO")) {
                        organizationEntities = organizationRepository.findHO();     // 타입에 해당하는 데이터 조회
                    } else if (searchType.equals("BO")) {
                        organizationEntities = organizationRepository.findBO();     // 타입에 해당하는 데이터 조회
                    }
                } else {                                         // 검색어와 타입이 존재한다면
                    if (searchType.equals("HO")) {
                        organizationEntities = organizationRepository.findHOName(searchWord);       // 검색어에 해당하는 데이터 조회
                    } else if (searchType.equals("BO")) {
                        organizationEntities = organizationRepository.findBOName(searchWord);       // 검색어에 해당하는 데이터 조회
                    }
                }

                // 4. 조회한 결과를 HTML에서 사용할 DTO로 변환
                List<OrganizationDTO> organizationDTOS = organizationEntities.stream()
                        .map(data -> modelMapper.map(data, OrganizationDTO.class) )
                        .collect(Collectors.toList());

                // 5. 결과값을 전달
                return organizationDTOS;

            }

        } catch (Exception e) {     //오류발생시 오류 처리
            throw new RuntimeException("조회 오류");
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : Page<OrganizationDTO> organList()
        인수 : Pageable page, String type, String keyword
        출력 : 받은 인수들로 조직 조회
        설명 : 사용자의 호텔검색
    ----------------------------------------------------------------------------- */
    public List<OrganizationDTO> hotelList(String location, String location2, String keyword) {

        System.out.println(location);
        System.out.println(location2);
        System.out.println(keyword);

        try {
            // 1. 조회
            // 조회결과를 저장할 변수
            List<OrganizationEntity> organizationEntities = new ArrayList<>();

            // 여러개를 조회해야 할땐 if문으로 분류따라 조회해야함
            if (keyword == null) {                           // 타입만 존재한다면
                if (location.equals("전체")) {
                    organizationEntities = organizationRepository.searchALLList();    // 타입에 해당하는 데이터 조회
                } else {
                    organizationEntities = organizationRepository.searchHotels(location, location2);    // 타입에 해당하는 데이터 조회
                }
            } else {                                         // 검색어와 타입이 존재한다면
                if (location.equals("전체")) {
                    organizationEntities = organizationRepository.searchALLNameList(keyword);      // 검색어에 해당하는 데이터 조회
                } else {
                    organizationEntities = organizationRepository.searchHotelsName(location, location2, keyword);      // 검색어에 해당하는 데이터 조회
                }

            }

            // 2. 조회한 결과를 HTML에서 사용할 DTO로 변환
            List<OrganizationDTO> organizationDTOS = organizationEntities.stream()
                    .map(entity -> modelMapper.map(entity, OrganizationDTO.class))
                    .collect(Collectors.toList());

            // 3. 결과값을 전달
            return organizationDTOS;

        } catch (Exception e) {     //오류발생시 오류 처리
            throw new RuntimeException("조회 오류");
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : void organDelete(Integer idx)
        인수 : 삭제할 조직 idx
        출력 : 없음
        설명 : 전달받은 idx의 값으로 데이터베이스를 조회하여 삭제
    ----------------------------------------------------------------------------- */
    public void organDelete(Integer idx, String join) {

        try {
            // 조직 조회
            Optional<OrganizationEntity> organizationOpt = organizationRepository.findById(idx);
            if (!organizationOpt.isPresent()) {
                throw new RuntimeException("조직을 찾을 수 없습니다.");
            }
            OrganizationEntity organization = organizationOpt.get();

            // 연관된 룸 삭제
            List<RoomEntity> rooms = roomRepository.findByOrganizationJoin_IdxOrderByRoomPriceDesc(idx);
            for (RoomEntity room : rooms) {
                // 룸 이미지 삭제
                List<ImageFileDTO> roomImageDTOs = imageFileService.readImage(room.getIdx(), "room");
                for (ImageFileDTO imageDTO : roomImageDTOs) {
                    imageFileService.deleteImage(imageDTO.getIdx());
                }
                // 룸 삭제
                roomRepository.delete(room);
            }

            // 조직 이미지 삭제
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(idx, join);
            List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                    .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                    .collect(Collectors.toList());
            for (ImageFileEntity imageFileEntity : imageFileEntities) {
                imageFileService.deleteImage(imageFileEntity.getIdx());
            }

            // 조직 삭제
            organizationRepository.deleteById(idx);

        } catch (Exception e) {
            throw new RuntimeException("삭제를 실패했습니다." + e.getMessage());
        }
    }

    public List<OrganizationDTO> getAllOrganizations() {
        List<OrganizationEntity> organizations = organizationRepository.findAll();
        List<OrganizationDTO> organizationDTOs = organizations.stream()
                .map(org -> new OrganizationDTO(org.getIdx(), org.getOrganName()))
                .collect(Collectors.toList());
        return organizationDTOs;
    }

    public Optional<OrganizationEntity> findById(Integer idx) {
        return organizationRepository.findById(idx);
    }

    // 호텔에서 가장 싼 방
    public RoomDTO CheapRoom(Integer idx) {
        // 가장 싼 방
        RoomEntity room = roomRepository.findCheap(idx)
                .orElseThrow(() -> new RuntimeException("방을 찾지 못했습니다."));

        // Entity -> DTO
        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        return roomDTO;
    }

}