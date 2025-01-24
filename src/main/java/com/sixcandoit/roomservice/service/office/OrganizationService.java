package com.sixcandoit.roomservice.service.office;

import com.sixcandoit.roomservice.dto.member.AdminDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.entity.member.AdminEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.entity.orders.CartEntity;
import com.sixcandoit.roomservice.repository.member.AdminRepository;
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
import org.springframework.ui.Model;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrganizationService {

    private final AdminRepository adminRepository;
    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;


    /* -----------------------------------------------------------------------------
        함수명 : void organRegister(OrganizationDTO organizationDTO)
        인수 : 입력 폼으로 부터 받는 OrganizationDTO
        출력 : 없음
        설명 : 전달받은 데이터를 데이터베이스에 저장하여 조직 등록
    ----------------------------------------------------------------------------- */
    public void organRegister(OrganizationDTO organizationDTO) {

        try {
            // DTO -> Entity로 변환
            OrganizationEntity organ = modelMapper.map(organizationDTO, OrganizationEntity.class);

            // Entity 테이블에 저장
            organizationRepository.save(organ);
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
    public void organUpdate(OrganizationDTO organizationDTO) {

        // DTO -> Entity로 변환
        OrganizationEntity organ = modelMapper.map(organizationDTO, OrganizationEntity.class);

        // Entity 테이블에 저장
        organizationRepository.save(organ);

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
        함수명 : Page<OrganizationDTO> organList(Pagealbe page)
        인수 : 입력 폼으로 부터 받는 OrganizationDTO
        출력 : 받은 OrganizationDTO를 테이블에 저장, 실패시 저장 안됨
        설명 : 조직을 등록
    ----------------------------------------------------------------------------- */
    public Page<AdminDTO> organList(Pageable page, String type, String keyword) {

        try {
            // 1. 페이지정보를 재가공
            int currentPage = page.getPageNumber() - 1;
            int pageSize = 5;

            // 기본 키로 내림차순해서 페이지 조회
            Pageable pageable = PageRequest.of(currentPage, pageSize,
                    Sort.by(Sort.Direction.ASC, "idx"));

            // 2. 조회
            // 조회결과를 저장할 변수
            Page<AdminEntity> adminEntity;

            // 여러개를 조회해야 할땐 if문으로 분류따라 조회해야함
            if (keyword == null) {                           // 타입만 존재한다면
                if (type.equals("HO")) {
                    adminEntity = adminRepository.searchHO(pageable);     // 타입에 해당하는 데이터 조회
                } else if (type.equals("BO")) {
                    adminEntity = adminRepository.searchBO(pageable);     // 타입에 해당하는 데이터 조회
                } else if (type.equals("SHOP")) {
                    adminEntity = adminRepository.searchSHOP(pageable);   // 타입에 해당하는 데이터 조회
                } else {
                    adminEntity = adminRepository.searchAll(pageable);      // 모든 데이터를 대상으로 조회
                }
            } else {                   // 검색어와 타입이 존재한다면
                if (type.equals("HO")) {
                    adminEntity = adminRepository.searchHOKey(keyword, pageable);   // 검색어에 해당하는 데이터 조회
                } else if (type.equals("BO")) {
                    adminEntity = adminRepository.searchBOKey(keyword, pageable);   // 검색어에 해당하는 데이터 조회
                } else if (type.equals("SHOP")) {
                    adminEntity = adminRepository.searchSHOPKey(keyword, pageable);   // 검색어에 해당하는 데이터 조회
                } else {
                    adminEntity = adminRepository.searchAll(pageable);      // 모든 데이터를 대상으로 조회
                }
            }

            // 3. 조회한 결과를 HTML에서 사용할 DTO로 변환
            Page<AdminDTO> adminDTO = adminEntity.map(entity -> {
                AdminDTO dto = modelMapper.map(entity, AdminDTO.class);

                // Organization 정보를 AdminDTO에 추가
                OrganizationDTO organizationDTO = modelMapper.map(entity.getOrganizationJoin(), OrganizationDTO.class);
                dto.setOrganDTO(organizationDTO);

                return dto;
            });

            // 4. 결과값을 전달
            return adminDTO;

        } catch (Exception e) {     //오류발생시 오류 처리
            throw new RuntimeException("조회 오류");
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : List<AdminDTO> adminList(String searchAdmin)
        인수 : 검색 모달에서 받은 searchAdmin 검색어
        출력 : searchAdmin로 조회된 회원 출력
        설명 : 관리자 회원 조회
    ----------------------------------------------------------------------------- */
    public List<AdminDTO> adminList(String searchAdmin) {

        try {
            // 1. 조회
            List<AdminEntity> adminEntities = adminRepository.searchAdminKey(searchAdmin);

            // 2. 조회한 결과를 HTML에서 사용할 DTO로 변환
            List<AdminDTO> adminDTO = adminEntities.stream().map(adminEntity -> modelMapper.map(adminEntity, AdminDTO.class)).collect(Collectors.toList());

            // 3. 결과값을 전달
            return adminDTO;

        } catch (Exception e) {     //오류발생시 오류 처리
            throw new RuntimeException("관리자 조회 오류");
        }

    }

    /* -----------------------------------------------------------------------------
        함수명 : void organDelete(Integer idx)
        인수 : 삭제할 조직 idx
        출력 : 없음
        설명 : 전달받은 idx의 값으로 데이터베이스를 조회하여 삭제
    ----------------------------------------------------------------------------- */
    public void organDelete(Integer idx) {

        // idx로 조회하여 삭제
        organizationRepository.deleteById(idx);

    }

}