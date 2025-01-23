package com.sixcandoit.roomservice.service.office;

import com.sixcandoit.roomservice.dto.office.ShopDetailDTO;
import com.sixcandoit.roomservice.entity.office.ShopDetailEntity;
import com.sixcandoit.roomservice.repository.office.ShopDetailRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopDetailService {

    private  final ShopDetailRepository shopDetailRepository;
    private  final ModelMapper modelMapper;

    /*-------------------------------------------------
    함수명 : register(ShopDetailDTO shopDetailDTO)
    인수 : shopDetailDTO
    출력 : 없음
    설명 : 매장 정보를 등록할때 사용
    ---------------------------------------------------*/
    public void register(ShopDetailDTO shopDetailDTO){
        try {
            //Optional<ShopDetailEntity> read = shopDetailRepository.findById(shopDTO.getIdx());
            ShopDetailEntity shopEntity = modelMapper.map(shopDetailDTO, ShopDetailEntity.class);
            shopDetailRepository.save(shopEntity);
        } catch (Exception e) {
            throw new RuntimeException("상점 저장 실패 : "+e.getMessage());
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
            Optional<ShopDetailEntity> read = shopDetailRepository.findById(idx);
            if(!read.isPresent()){
                throw new RuntimeException("상점 개별 조회 실패");
            }
            else {
                ShopDetailDTO   shopDetailDTO = modelMapper.map(read,ShopDetailDTO.class);
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
    public void update(ShopDetailDTO shopDetailDTO){
        try {
            Optional<ShopDetailEntity> read = shopDetailRepository.findById(shopDetailDTO.getIdx());
            if(!read.isPresent()){
                throw new RuntimeException("상점 조회 실패");
            }
            else {
                ShopDetailEntity shopEntity =modelMapper.map(shopDetailDTO,ShopDetailEntity.class);
                shopDetailRepository.save(shopEntity);
            }
        } catch (Exception e){
            throw new RuntimeException("상점 수정 실패: "+e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : delete(ShopDetailDTO shopDetail)
    인수 : shopDetailDTO
    출력 : 없음
    설명 : 매장 정보를 수정할때 사용
    ---------------------------------------------------*/
    public void delete(Integer idx){
        try {
            shopDetailRepository.deleteById(idx);
        } catch (Exception e){
            throw new RuntimeException("상점 삭제 실패: "+e.getMessage());
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