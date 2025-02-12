package com.sixcandoit.roomservice.service.orders;

import com.sixcandoit.roomservice.dto.orders.MenuDTO;
import com.sixcandoit.roomservice.entity.orders.MenuEntity;
import com.sixcandoit.roomservice.repository.orders.MenuRepository;
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

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MenuService {

    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;

    //메뉴 등록
    public Integer menuRegister(MenuDTO menuDTO, List<MultipartFile> multipartFiles) {

        //MenuDTO -> Entity로 변환
        MenuEntity menuEntity = modelMapper.map(menuDTO, MenuEntity.class);

        menuEntity = menuRepository.save(menuEntity);

        //이미지 등록은 추후 추가


        return menuEntity.getIdx();

    }

    //메뉴 읽기
    public MenuDTO menuRead(Integer idx) {

        MenuEntity menuEntity
                = menuRepository.findById(idx).orElseThrow(EntityNotFoundException::new);

        MenuDTO menuDTO = modelMapper.map(menuEntity, MenuDTO.class);   //이미지 등록 추후 추가

        return menuDTO;

    }

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
            if (keyword != null) {  //검색어가 존재하면
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

    //메뉴 수정
    public MenuDTO menuUpdate(MenuDTO menuDTO, Integer idx, List<MultipartFile> multipartFiles, Integer[] delidx, Long mainidx) {
        //menuDTO 수정
        MenuEntity menuEntity =
                menuRepository.findById(menuDTO.getIdx())
                        .orElseThrow(EntityNotFoundException::new);

        //set 수정된 값을 Menu 객체에 반영
        menuEntity.setMenuName(menuDTO.getMenuName());
        menuEntity.setMenuContent(menuDTO.getMenuContent());
        menuEntity.setMenuPrice(menuDTO.getMenuPrice());
        //menuEntity.setMenuImg(menuDTO.getMenuImg()); //이미지는 추후 추가
        menuEntity.setMenuOptionYn(menuDTO.getMenuOptionYn());
        menuEntity.setActiveYn(menuDTO.getActiveYn());
        menuEntity.setMenuSalesYn(menuDTO.getMenuSalesYn());
        menuEntity.setMenuSaleType(menuDTO.getMenuSaleType());
        menuEntity.setMenuSaleAmount(menuDTO.getMenuSaleAmount());
        menuEntity.setMenuSalePercent(menuDTO.getMenuSalePercent());

        //수정된 Menu객체를 DTO로 변환하여 반환
        MenuDTO menuUpdateDTO = new MenuDTO();
        menuUpdateDTO.setIdx(menuEntity.getIdx());
        menuUpdateDTO.setMenuName(menuEntity.getMenuName());
        menuUpdateDTO.setMenuContent(menuEntity.getMenuContent());
        menuUpdateDTO.setMenuPrice(menuEntity.getMenuPrice());
        //menuUpdateDTO.setMenuImg(menuEntity.getMenuImg()); //이미지는 추후 추가
        menuUpdateDTO.setMenuOptionYn(menuEntity.getMenuOptionYn());
        menuUpdateDTO.setActiveYn(menuEntity.getActiveYn());
        menuUpdateDTO.setMenuSalesYn(menuEntity.getMenuSalesYn());
        menuUpdateDTO.setMenuSaleType(menuEntity.getMenuSaleType());
        menuUpdateDTO.setMenuSaleAmount(menuEntity.getMenuSaleAmount());
        menuUpdateDTO.setMenuSalePercent(menuEntity.getMenuSalePercent());

        return menuUpdateDTO;   //수정된 MenuDTO 반환
    }

    public void menuRemove(Integer idx) {
        log.info("서비스로 들어온 삭제할 메뉴 번호 : " + idx);

        menuRepository.deleteById(idx);
    }



}
