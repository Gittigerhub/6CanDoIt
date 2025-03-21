package com.sixcandoit.roomservice.service.office;

import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.dto.office.ShopDetailDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.entity.office.ShopDetailEntity;
import com.sixcandoit.roomservice.repository.office.OrganizationRepository;
import com.sixcandoit.roomservice.repository.office.ShopDetailRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopDetailService {

    private final ShopDetailRepository shopDetailRepository;
    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;
    private final ImageFileService imageFileService;

    /*-------------------------------------------------
    함수명 : register(ShopDetailDTO shopDetailDTO)
    인수 : shopDetailDTO
    출력 : 없음
    설명 : 매장 정보를 등록할때 사용
    ---------------------------------------------------*/
    public void register(ShopDetailDTO shopDetailDTO, Integer organIdx) {
        try {
            System.out.println(shopDetailDTO.toString());
            System.out.println(organIdx);

            // 조직정보 조회
            OrganizationEntity organEntity = organizationRepository.findById(organIdx)
                    .orElseThrow(() -> new EntityNotFoundException("해당 조직을 찾을 수 없습니다: " + organIdx));;

            System.out.println("1");

            // DTO => Entity 변환
            ShopDetailEntity shopDetailEntity = new ShopDetailEntity();
            shopDetailEntity.setOpenTime(shopDetailDTO.getOpenTime());
            shopDetailEntity.setCloseTime(shopDetailDTO.getCloseTime());
            shopDetailEntity.setRestDay(shopDetailDTO.getRestDay());
            shopDetailEntity.setOpenState(shopDetailDTO.getOpenState());
            shopDetailEntity.setPartnerState(shopDetailDTO.getPartnerState());
            shopDetailEntity.setType(shopDetailDTO.getType());
            shopDetailEntity.setBankNum(shopDetailDTO.getBankNum());
            shopDetailEntity.setBankName(shopDetailDTO.getBankName());
            shopDetailEntity.setBankOwner(shopDetailDTO.getBankOwner());
            shopDetailEntity.setDayFee(shopDetailDTO.getDayFee());
            shopDetailEntity.setDayFeePercent(shopDetailDTO.getDayFeePercent());

            System.out.println("2");

            // 연관관계 데이터 추가
            shopDetailEntity.setOrganizationJoin(organEntity);

            System.out.println("3");

            // DB에 저장
            shopDetailRepository.save(shopDetailEntity);

        } catch (Exception e) {
            throw new RuntimeException("상점 저장 실패 : "+e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : findOrgan(Integer idx)
    인수 : Integer idx
    출력 : 매장 데이터
    설명 : 조직 idx로 매장 정보 조회할때 사용
    ---------------------------------------------------*/
    public ShopDetailDTO findOrgan(Integer idx) {
        try {
            // idx로 데이터 조회
            Optional<ShopDetailEntity> read = shopDetailRepository.findOrgan(idx);

            if(!read.isPresent()){                              // 데이터가 없으면
                throw new RuntimeException("조회 실패");
            }
            else {                                              // 데이터가 있으면
                // Entity => DTO 변환
                ShopDetailDTO shopDetailDTO = modelMapper.map(read ,ShopDetailDTO.class);

                // 반환
                return  shopDetailDTO;
            }

        } catch (Exception e) {
            throw new RuntimeException("상점 저장 실패 : "+e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : shopCheck(Integer idx)
    인수 : Integer idx
    출력 : boolean
    설명 : 매장 데이터가 있는지 확인하는 메서드
    ---------------------------------------------------*/
    public boolean shopCheck(Integer idx) {
        System.out.println(idx);
        System.out.println(idx);
        System.out.println(idx);
        try {
            // idx로 데이터 조회
            Optional<ShopDetailEntity> read = shopDetailRepository.findOrgan(idx);

            if (!read.isPresent()) {
                // 데이터가 없을 경우 처리

                return false;
            }

            return true;

        } catch (Exception e) {
            throw new RuntimeException("데이터 존재여부 확인 실패 : "+e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : read(int idx)
    인수 : 조회할 매장 idx
    출력 : ShopDetailDTO
    설명 : 매장을 조회할 때 사용
  ---------------------------------------------------*/
    public ShopDetailDTO read(Integer idx){
        try {
            // idx로 데이터 조회
            Optional<ShopDetailEntity> read = shopDetailRepository.findById(idx);

            if(!read.isPresent()){                              // 데이터가 없으면
                throw new RuntimeException("상점 개별 조회 실패");
            }
            else {                                              // 데이터가 있으면
                // Entity => DTO 변환
                ShopDetailDTO shopDetailDTO = modelMapper.map(read ,ShopDetailDTO.class);

                // 반환
                return  shopDetailDTO;
            }
         } catch (Exception e){
            throw new RuntimeException("상점 개별 조회 실패: "+e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : update(ShopDetailDTO shopDetail)
    인수 : shopDetailDTO
    출력 : 없음
    설명 : 매장 정보를 수정할때 사용;
  ---------------------------------------------------*/
    public void update(ShopDetailDTO shopDetailDTO, OrganizationDTO organizationDTO, String join, List<MultipartFile> imageFiles, Integer organIdx){
        System.out.println("체크 3");
        System.out.println("shopDetailDTO : " + shopDetailDTO.toString());
        System.out.println("organizationDTO : " + organizationDTO.toString());
        System.out.println("imageFiles : " + imageFiles.toString());
        try {
            // organizationDTO.getIdx()로 조직 데이터 조회
            OrganizationEntity organ = organizationRepository.findById(organIdx)
                    .orElseThrow(() -> new RuntimeException("수정할 조직 조회 실패"));

            System.out.println("체크 4");
            System.out.println("organ : " + organ.toString());

            // shopDetailDTO.getIdx()로 매장 데이터 조회
            ShopDetailEntity shopEntity = shopDetailRepository.findById(shopDetailDTO.getIdx())
                    .orElseThrow(() -> new RuntimeException("상점 조회 실패"));

            System.out.println("organ : " + organ.getIdx());
            System.out.println("shopEntity : " + shopEntity.getIdx());
            System.out.println("체크 5");

            // 빠진 idx값 주입
            organizationDTO.setIdx(organIdx);
            System.out.println(organIdx);
            System.out.println("organizationDTO : " + organizationDTO.toString());
            System.out.println("shopDetailDTO : " + shopDetailDTO.toString());
            System.out.println("체크 6");

            // DTO -> Entity로 변환
            OrganizationEntity organization = modelMapper.map(organizationDTO, OrganizationEntity.class);
            organization.setAdminJoin(organ.getAdminJoin());
            organization.setShopDetailJoin(organ.getShopDetailJoin());
            organization.setAdvertisementJoin(organ.getAdvertisementJoin());
            organization.setMenuJoin(organ.getMenuJoin());
            organization.setRoomJoin(organ.getRoomJoin());
            organization.setEventJoin(organ.getEventJoin());
            organization.setImageFileJoin(organ.getImageFileJoin());
            organization.setHotels(organ.getHotels());

            ShopDetailEntity shopDetail = modelMapper.map(shopDetailDTO, ShopDetailEntity.class);
            shopDetail.setMenuJoin(shopEntity.getMenuJoin());
            shopDetail.setCalculateJoins(shopEntity.getCalculateJoins());
            shopDetail.setOrganizationJoin(shopEntity.getOrganizationJoin());

            System.out.println("organ : " + organ.getIdx());
            System.out.println("shopEntity : " + shopEntity.getIdx());
            System.out.println("체크 7");

            // 이미지 추가 등록
            List<ImageFileEntity> images =
                    imageFileService.updateImage(imageFiles, join, organizationDTO.getIdx());

            System.out.println("체크 8");

            // 이미지 정보 추가
            // 양방향 연관관계 편의 메서드 사용
            if (images != null && !images.isEmpty()) {
                for (ImageFileEntity image : images) {
                    organ.addImage(image);  // FK 자동 설정
                }
            }

            System.out.println("체크 9");

            // Entity 테이블에 저장
            shopDetailRepository.save(shopDetail);

            System.out.println("체크 10");

            // Entity 테이블에 저장
            organizationRepository.save(organization);

            System.out.println("체크 11");

        } catch (Exception e) {
            throw new RuntimeException("수정서비스 실패: " + e.getMessage());
        }

    }

    /*-------------------------------------------------
    함수명 : list(Pageable page)
    인수 : Pageable
    출력 : 목록
    설명 : 매장 정보들을 목록을 출력할때 사용
    ---------------------------------------------------*/
    public List<ShopDetailDTO> list(){
        List<ShopDetailEntity> shoplist = shopDetailRepository.findAll();

        List<ShopDetailDTO> shopDTOlist = Arrays.asList(modelMapper.map(shoplist, ShopDetailDTO[].class));

        return shopDTOlist;
    }

}