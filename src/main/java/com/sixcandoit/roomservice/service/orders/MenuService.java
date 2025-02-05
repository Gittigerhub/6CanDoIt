package com.sixcandoit.roomservice.service.orders;

import com.sixcandoit.roomservice.dto.orders.MenuDTO;
import com.sixcandoit.roomservice.entity.orders.MenuEntity;
import com.sixcandoit.roomservice.repository.orders.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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
    public Integer menuRegister(MenuDTO menuDTO) {

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
