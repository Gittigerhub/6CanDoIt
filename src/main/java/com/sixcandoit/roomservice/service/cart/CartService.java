package com.sixcandoit.roomservice.service.cart;

import com.sixcandoit.roomservice.dto.Menu.MenuDTO;
import com.sixcandoit.roomservice.dto.cart.CartDetailDTO;
import com.sixcandoit.roomservice.dto.cart.CartOrdersDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.cart.CartEntity;
import com.sixcandoit.roomservice.entity.cart.CartMenuEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
import com.sixcandoit.roomservice.entity.room.ReservationEntity;
import com.sixcandoit.roomservice.repository.ImageFileRepository;
import com.sixcandoit.roomservice.repository.cart.CartMenuRepository;
import com.sixcandoit.roomservice.repository.cart.CartRepository;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.repository.menu.MenuRepository;
import com.sixcandoit.roomservice.repository.room.ReservationRepository;
import com.sixcandoit.roomservice.service.orders.OrdersService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ModelMapper modelMapper;

    private final ReservationRepository reservationRepository;

    private final OrdersService ordersService;

    private final MenuRepository menuRepository;
    //메뉴를 찾아야한다. findByIdx(menuEntity.idx)

    private final MemberRepository memberRepository;
    //누구의 장바구니인지 찾아야한다.

    private final CartRepository cartRepository;
    //장바구니 생성 여부 확인 및 장바구니 생성
    private final CartMenuRepository cartMenuRepository;
    //장바구니에 넣을 장바구니메뉴를 만들려면 메뉴를 참조.
    //참조한 메뉴를 가지고 장바구니 메뉴를 만들어야 한다.

    private final ImageFileRepository imageFileRepository;

    //등록_장바구니 만들기
    //장바구니를 따로 생성하지는 않고, 장바구니에 넣을 메뉴가 컨트롤러로 들어오면
    //해당 값을 가지고 넣을 것이고, 컨트롤러에서 들어오는 email을 통해서 멤버를 찾게 할 예정
    public Integer addCart(Integer idx, String memberEmail, int count) {
        //회원 찾기
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다: " + memberEmail));

        // 예약 찾기
        ReservationEntity reservationEntity = reservationRepository.CheckInReserv(memberEntity.getIdx())
                .orElseThrow(() -> new EntityNotFoundException("이용중인 예약건을 찾을 수 없습니다."));

        //메뉴 검증
        MenuEntity menuEntity = menuRepository.findById(idx)
                .orElseThrow(() -> new EntityNotFoundException("선택한 메뉴를 찾을 수 없습니다."));

        //메뉴정보를 MenuDTO로 변환
        MenuDTO menuDTO = modelMapper.map(menuEntity, MenuDTO.class);

        // 메뉴 이미지 파일 가져오기
        List<ImageFileEntity> imageFileEntities = imageFileRepository.menuJoin(menuEntity.getIdx());

        // 변환된 ImageFileDTO 리스트를 menuDTO에 설정
        menuDTO.setMenuImgDTOList(imageFileEntities);  // List<ImageFileDTO> 전달

        // 카트 찾기
        CartEntity cartEntity = cartRepository.findByMemberJoin(memberEntity.getIdx())
                .orElseThrow(() -> new EntityNotFoundException("카트가 없습니다."));

        // 카트 없으면 새로 생성
        if (cartEntity == null) {
            cartEntity = CartEntity.createCartEntity(memberEntity, reservationEntity);
            cartRepository.save(cartEntity);
        }

        // 카트idx를 FK로 카트메뉴를 생성한다.
        CartMenuEntity createCartMenu = CartMenuEntity.createCartMenuEntity(cartEntity, menuEntity, count);

        // 카트메뉴가 있읅경우엔 가져온다.
        CartMenuEntity saveCartMenu
                = cartMenuRepository.findByCartEntity_IdxAndIdx(cartEntity.getIdx(), idx);

        // 동일한 메뉴가 장바구니에 있다면 해당 메뉴의 수량이 증가하도록
        if (saveCartMenu != null) {
            //수량 증가
            saveCartMenu.addCount(1);
            //저장된 장바구니에서 장바구니 메뉴 pk를 반환
            return saveCartMenu.getIdx();
        }else {
            CartMenuEntity cartMenuEntity
                    = CartMenuEntity.createCartMenuEntity(cartEntity, menuEntity, 1);

            //장바구니에 장바구니 메뉴 저장
            cartMenuRepository.save(cartMenuEntity);

            return cartMenuEntity.getIdx();
        }
    }

    //장바구니 상세 리스트 반환(CartDetailDTO로 반환)
    public List<CartDetailDTO> cartDetailDTOList(String memberEmail) {

        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();

        // 회원 찾기
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다: " + memberEmail));

        if (memberEntity == null) {
            // 만약 회원이 없다면 빈 리스트 반환 (혹은 적절한 예외 처리)
            return cartDetailDTOList;
        }

        // 카트 조회
        CartEntity cartEntity = cartRepository.findByMemberJoin(memberEntity.getIdx())
                .orElseThrow(() -> new EntityNotFoundException("회원의 카트를 찾을 수 없습니다."));

        //장바구니에 담겨있는 메뉴를 조회
        //cartDetailDTOList = cartMenuRepository.findByCartDetailDTOList(cartEntity.getIdx());
        List<CartMenuEntity> cartMenuEntities = cartMenuRepository.findByCartEntity_Idx(cartEntity.getIdx());

        // CartMenuEntity에서 CartDetailDTO로 변환
        for (CartMenuEntity cartMenuEntity : cartMenuEntities) {
            MenuEntity menuEntity = cartMenuEntity.getMenuEntity();

            // MenuEntity에서 값을 가져와 CartDetailDTO 생성
            CartDetailDTO cartDetailDTO = new CartDetailDTO();
            cartDetailDTO.setCartMenuIdx(cartMenuEntity.getIdx());
            cartDetailDTO.setMenuName(menuEntity.getMenuName());
            cartDetailDTO.setMenuPrice(menuEntity.getMenuPrice());
            cartDetailDTO.setCount(cartMenuEntity.getCount());

            //MenuEntity에서 첫 번째 이미지 경로를 가져온다
            if (!menuEntity.getImageFileJoin().isEmpty()) {
                cartDetailDTO.setMenuImg(menuEntity.getImageFileJoin().get(0).getUrl());
            } else {
                cartDetailDTO.setMenuImg(null); //이미지가 없으면 null로 처리
            }

            //리스트에 추가
            cartDetailDTOList.add(cartDetailDTO);
        }

        return cartDetailDTOList;
    }

    //장바구니 메뉴 유효성 검사
    public boolean validateCartMenu(Integer cartMenuidx,String memberEmail) {
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(memberEmail);

        CartMenuEntity cartMenuEntity
                = cartMenuRepository.findById(cartMenuidx).orElseThrow(EntityNotFoundException::new);

        if (memberEntity != null && cartMenuEntity != null) {
            if (!memberEntity.get().getMemberEmail().equals(cartMenuEntity.getCartEntity().getMemberJoin().getMemberEmail())) {
                return false;
            }
        }
        return true;
    }

    //장바구니 메뉴 삭제
    public void deleteCartMenu(Integer cartMenuidx) {
        CartMenuEntity cartMenuEntity
                = cartMenuRepository.findById(cartMenuidx).orElseThrow(EntityNotFoundException::new);
        cartMenuRepository.delete(cartMenuEntity);
    }


    //장바구니에서 들어온 주문
    public Integer orderCartMenu(List<CartOrdersDTO> cartOrdersDTOList, String memberEmail) {
        //cartOrderDTOList cartMenuIdx가 들어있음

        List<OrdersDTO> ordersDTOList = new ArrayList<>();

        for (CartOrdersDTO cartOrdersDTO : cartOrdersDTOList) {
            //cartItemIdx를 하나씩 가지고

            CartMenuEntity cartMenuEntity
                    = cartMenuRepository.findById(cartOrdersDTO.getCartMenuIdx())
                        .orElseThrow(EntityNotFoundException::new);

            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setMenuIdx(cartMenuEntity.getMenuEntity().getIdx());
            ordersDTO.setCount(cartMenuEntity.getCount());

            ordersDTOList.add(ordersDTO);
        }
        Integer ordersIdx
                = ordersService.orders(ordersDTOList, memberEmail);   //장바구니 메뉴들 저장

        //주문 생성 후 카트에서 해당 메뉴 삭제
        for (CartOrdersDTO cartOrdersDTO : cartOrdersDTOList) {
            CartMenuEntity cartMenuEntity
                    = cartMenuRepository.findById(cartOrdersDTO.getCartMenuIdx())
                    .orElseThrow(EntityNotFoundException::new);


            cartMenuRepository.delete(cartMenuEntity);
        }
        return ordersIdx;   //주문 ID 반환
    }
}
