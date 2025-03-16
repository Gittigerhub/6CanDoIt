package com.sixcandoit.roomservice.service.menu;

import com.sixcandoit.roomservice.constant.MenuCategory;
import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.Menu.MenuDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.dto.office.ShopDetailDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.entity.office.ShopDetailEntity;
import com.sixcandoit.roomservice.entity.room.ReservationEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.repository.menu.MenuRepository;
import com.sixcandoit.roomservice.repository.office.OrganizationRepository;
import com.sixcandoit.roomservice.repository.office.ShopDetailRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.office.ShopDetailService;
import com.sixcandoit.roomservice.service.room.ReservationService;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MenuService {

    private final ReservationService reservationService;
    private final MemberRepository memberRepository;
    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;
    private final OrganizationRepository organizationRepository;
    private final ShopDetailRepository shopDetailRepository;

    //이미지 등록할 menuImgService 의존성 추가
    private final ImageFileService imageFileService;
    private final ShopDetailService shopDetailService;


    // 회원 찾아서 예약건 찾아오기
    public OrganizationDTO findMemberRes(String email) {

        // 회원 이메일로 회원 조회
        MemberEntity memberEntity = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원정보를 찾을 수 없습니다."));

        // 찾아온 회원정보로 예약건 찾기
        ReservationEntity reservationEntities = reservationService.findCheckInRes(memberEntity.getIdx());

        if (reservationEntities == null) {
            System.out.println("예약완료된 예약건이 없습니다.");
        }

        // 예약완료된 예약건의 호텔 찾기
        OrganizationEntity organization = organizationRepository.findById(reservationEntities.getRoomJoin().getOrganizationJoin().getIdx())
                .orElseThrow(() -> new RuntimeException("조직을 조회할 수 없습니다."));

        // Entity -> DTO
        OrganizationDTO organizationDTO = modelMapper.map(organization, OrganizationDTO.class);
        // 반환하기
        return organizationDTO;

    }

    //메뉴 등록
    public Integer menuRegister(MenuDTO menuDTO, List<MultipartFile> multipartFiles,
                                Integer hotelsId, Integer shopDetailIdx) throws Exception {

        try {

            // MenuDTO -> MenuEntity로 변환
            MenuEntity menuEntity = modelMapper.map(menuDTO, MenuEntity.class);

            // 매장 정보 조회
            ShopDetailDTO shopDetailDTO = shopDetailService.read(shopDetailIdx);
            ShopDetailEntity shopDetailEntity = modelMapper.map(shopDetailDTO, ShopDetailEntity.class);

            // 본사/지사(hotels_id) 조직 정보 조회
            OrganizationEntity hotelOrganization = organizationRepository.findById(hotelsId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 본사/지사를 찾을 수 없습니다: " + hotelsId));

            // 매장과 본사/지사 정보 설정
            menuEntity.setShopDetailJoin(shopDetailEntity);
            menuEntity.setOrganizationJoin(hotelOrganization);  // hotels_id에 해당하는 조직으로 설정


            // 이미지 등록
            List<ImageFileEntity> images = imageFileService.saveImages(multipartFiles);

            //이미지 정보 추가
            //양방향 연관관계 편의 메서드 사용
            for (ImageFileEntity image : images) {
                menuEntity.addImage(image); //FK 자동 설정
            }
            System.out.println("FK 자동 등록");

            //Entity 테이블에 저장
            imageFileService.saveImages(multipartFiles);
            System.out.println("진짜 저장됨");

            // 메뉴 저장 (DB에 메뉴 정보 저장)
            menuEntity = menuRepository.save(menuEntity);

            return menuEntity.getIdx(); // 메뉴의 idx 반환

        } catch (Exception e) {   //오류발생시 오류 처리
            throw new RuntimeException("등록 실패");
        }

    }


    //메뉴 읽기
    public MenuDTO menuRead(Integer idx) {

        if (idx == null) {
            throw new RuntimeException("menuID가 없습니다.");
        }
        // 메뉴 정보 조회
        MenuEntity menuEntity
                = menuRepository.findById(idx).orElseThrow(EntityNotFoundException::new);

        MenuDTO menuDTO = modelMapper.map(menuEntity, MenuDTO.class)
                .setMenuImgDTOList(menuEntity.getImageFileJoin());

        return menuDTO;

    }

    //목록 검색
    public Page<MenuDTO> menuList(Pageable page, String type, String keyword) {
        try {
            // 1. 페이지 정보를 재가공
            int currentPage = page.getPageNumber() - 1;  // 화면의 페이지 번호를 db 페이지 번호로
            int pageSize = 10; // 한 페이지를 구성하는 레코드 수

            // 지정된 내용으로 페이지 정보를 재생산, 정렬 내림차순 DESC
            Pageable pageable = PageRequest.of(currentPage, pageSize,
                    Sort.by(Sort.Direction.DESC, "idx"));

            // 2. 조회
            // 조회 결과를 저장할 변수 선언
            Page<MenuEntity> menuEntities;

            // 여러개를 조회해야 할 땐 if문으로 분류에 따라 조회해야한다.
            // type : 메뉴명(1), 메뉴설명(2), 메뉴명+메뉴설명(3), 카테고리(4), 전체(0)
            if (keyword != null && !keyword.isEmpty()) {  //검색어가 존재하면
                log.info("검색어가 존재하면...");
                if (type.equals("1")) { //type 분류 1, 메뉴명으로 검색할 때
                    log.info("메뉴명으로 검색 하는 중...");
                    menuEntities = menuRepository.searchMenuTitle(keyword, pageable);
                } else if (type.equals("2")) { //type 분류 2, 메뉴설명으로 검색할 때
                    log.info("메뉴설명으로 검색 하는 중...");
                    menuEntities = menuRepository.searchMenuContent(keyword, pageable);
                } else if (type.equals("3")) { //type 분류 3, 메뉴명+메뉴설명으로 검색할 때
                    log.info("메뉴명+메뉴설명으로 검색 하는 중...");
                    menuEntities = menuRepository.searchMenuNameAndMenuContent(keyword, pageable);
                } else if (type.equals("4")) { //type 분류 4, 카테고리로 검색할 때
                    log.info("카테고리로 검색 하는 중...");
                    menuEntities = menuRepository.searchMenuCategory(keyword, pageable);
                } else { //type 분류 5, 전체로 검색할 때
                    log.info("전체 조회 검색 중...");
                    menuEntities = menuRepository.searchMenuAll(keyword, pageable);
                }
            }else { //검색어가 존재하지 않으면 모두 검색
                    menuEntities = menuRepository.findAll(pageable);

            }

            // 3. 조회한 결과를 HTML에서 사용할 DTO로 변환
            //Entity를 dTO로 변환 후 저장

            Page<MenuDTO> menuDTOS = menuEntities.map(
                    data -> modelMapper.map(data, MenuDTO.class));

            // 4. 결과값을 전달
            return menuDTOS;

        } catch (Exception e) { //오류 발생시 처리
            throw new RuntimeException("조회 오류");
        }

    }

    // 사용자 룸서비스 목록
    public Page<MenuDTO> menuMemberList(Pageable page, String type, String keyword, Integer organIdx) {
        try {
            // 1. 페이지 정보를 재가공
            int currentPage = page.getPageNumber() - 1;  // 화면의 페이지 번호를 db 페이지 번호로
            int pageSize = 10; // 한 페이지를 구성하는 레코드 수

            // 지정된 내용으로 페이지 정보를 재생산, 정렬 내림차순 DESC
            Pageable pageable = PageRequest.of(currentPage, pageSize,
                    Sort.by(Sort.Direction.DESC, "idx"));

            // 2. 조회
            // 조회 결과를 저장할 변수 선언
            Page<MenuEntity> menuEntities = menuRepository.AllMenu(pageable, organIdx);

            // 3. 조회한 결과를 HTML에서 사용할 DTO로 변환
            //Entity를 dTO로 변환 후 저장

            Page<MenuDTO> menuDTOS = menuEntities.map(
                    data -> modelMapper.map(data, MenuDTO.class));

            // 4. 결과값을 전달
            return menuDTOS;

        } catch (Exception e) { //오류 발생시 처리
            throw new RuntimeException("조회 오류");
        }

    }

    //카테고리 검색
    public Page<MenuDTO> selectCate(Pageable page, String category) {

        try {
            // 1. 페이지 정보를 재가공
            int currentPage = page.getPageNumber() - 1;  // 화면의 페이지 번호를 db 페이지 번호로
            int pageSize = 10; // 한 페이지를 구성하는 레코드 수

            // 지정된 내용으로 페이지 정보를 재생산, 정렬 내림차순 DESC
            Pageable pageable = PageRequest.of(currentPage, pageSize,
                    Sort.by(Sort.Direction.DESC, "idx"));

            // 2. Repository에서 enum조회를 위한 category enum타입으로 변환
            MenuCategory categoryEnum = MenuCategory.valueOf(category);

            // 3. 조회
            // 조회 결과를 저장할 변수 선언
            Page<MenuEntity> menuEntities = menuRepository.selectCate(categoryEnum, pageable);

            // 4. 조회한 결과를 HTML에서 사용할 DTO로 변환
            // Entity를 dTO로 변환 후 저장
            Page<MenuDTO> menuDTOS = menuEntities.map(
                    data -> modelMapper.map(data, MenuDTO.class));

            // 5. 결과값을 전달
            return menuDTOS;

        } catch (Exception e) { //오류 발생시 처리
            throw new RuntimeException("조회 오류");
        }

    }

    //메뉴 수정
    public void menuUpdate(MenuDTO menuDTO, String join, List<MultipartFile> multipartFiles) {

        try {
            // DTO => Entity 변환
            MenuEntity menuEntity =
                    menuRepository.findById(menuDTO.getIdx())
                            .orElseThrow(EntityNotFoundException::new);

            // ModelMapper를 사용하여 DTO의 데이터를 엔티티에 반영
            modelMapper.map(menuDTO, menuEntity);

            // 기존 이미지들 업데이트 (새로운 이미지들 추가)
            List<ImageFileEntity> updateImages
                    = imageFileService.updateImage(multipartFiles, join, menuDTO.getIdx());

            // 이미지 정보 추가
            // 양방향 연관관계 편의 메서드 사용
            for (ImageFileEntity image : updateImages) {
                menuEntity.addImage(image);  // FK 자동 설정
            }

            //menu Entity 업데이트
            menuRepository.save(menuEntity);    //엔티티를 DB에 저장

        } catch (Exception e) {
            throw new RuntimeException("수정서비스 실패" + e.getMessage());
        }

    }

    public void menuRemove(Integer idx, String join) {
        log.info("서비스로 들어온 삭제할 메뉴 번호 : " + idx);

        try {
            //이미지 조회
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(idx, join);

            //dto -> Entity 변환
            List<ImageFileEntity> imageFileEntities = imageFileDTOS.stream()
                            .map(imageFileDTO -> modelMapper.map(imageFileDTO, ImageFileEntity.class))
                            .collect(Collectors.toList());
            //모든 이미지 삭제
            for (ImageFileEntity imageFileEntity : imageFileEntities) {
                imageFileService.deleteImage(imageFileEntity.getIdx());
            }

            //idx로 조회해서 삭제
            menuRepository.deleteById(idx);

        } catch (Exception e) {
            throw new RuntimeException("삭제에 실패했습니다." + e.getMessage());
        }

    }

}