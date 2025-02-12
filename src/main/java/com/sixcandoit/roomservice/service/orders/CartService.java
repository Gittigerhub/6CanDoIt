package com.sixcandoit.roomservice.service.orders;

import com.sixcandoit.roomservice.dto.orders.CartMenuDTO;
import com.sixcandoit.roomservice.dto.orders.CartOrdersDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.orders.CartEntity;
import com.sixcandoit.roomservice.entity.orders.CartMenuEntity;
import com.sixcandoit.roomservice.entity.orders.MenuEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.repository.orders.CartMenuRepository;
import com.sixcandoit.roomservice.repository.orders.CartRepository;
import com.sixcandoit.roomservice.repository.orders.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CartService {

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

    //등록_장바구니 만들기
    //장바구니를 따로 생성하지는 않고, 장바구니에 넣을 메뉴가 컨트롤러로 들어오면
    //해당 값을 가지고 넣을 것이고, 컨트롤러에서 들어오는 email을 통해서 멤버를 찾게 할 예정
    public Integer addCart(CartMenuDTO cartMenuDTO, String memberEmail) {
        //회원 찾기
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(memberEmail);

        //메뉴 검증
        MenuEntity menuEntity = menuRepository.findById(cartMenuDTO.getMenuidx())
                .orElseThrow(EntityNotFoundException::new);

        //카트 찾기
        CartEntity cartEntity
                = cartRepository.findByMemberJoin_memberEmail(memberEntity.get().getMemberEmail());

        //카트 없으면 새로 생성
        if (cartEntity == null) {
            cartEntity = CartEntity.createCartEntity(memberEntity.get(), cartEntity.getCartMenuCount());
            cartRepository.save(cartEntity);
        }

        //카트가 없으면 새로 생성, 있다면 있는걸로
        //장바구니 메뉴를 만들어 넣어주고, 저장한다.
        CartMenuEntity saveCartMenu
                = cartMenuRepository.findByCartEntity_IdxAndIdx(cartEntity.getIdx(), cartMenuDTO.getMenuidx());

        //동일한 메뉴가 장바구니에 있다면 해당 메뉴의 수량이 증가하도록
        if (saveCartMenu != null) {
            //수량 증가
            saveCartMenu.addCount(cartMenuDTO.getCount());
            //저장된 장바구니에서 장바구니 메뉴 pk를 반환
            return saveCartMenu.getIdx();
        }else {
            CartMenuEntity cartMenuEntity
                    = CartMenuEntity.createCartMenuEntity(cartEntity, menuEntity, cartMenuDTO.getCount());

            //장바구니에 장바구니 메뉴 저장
            cartMenuRepository.save(cartMenuEntity);

            return cartMenuEntity.getIdx();
        }
    }

//    public List<CartDetailDTO> cartDetailDTOList(String memberEmail) {
//
//        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();
//
//        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(memberEmail);
//
//        CartEntity cartEntity = cartRepository.findByMemberJoin_memberEmail(memberEntity.get().getMemberEmail());
//
//        //카트가 존재하지 않는다면
//        if (cartEntity == null) {
//            return cartDetailDTOList;
//        }
//        //장바구니에 담겨있는 메뉴를 조회
//        cartDetailDTOList = cartMenuRepository.findByCartDetailDTOList(cartEntity.getIdx());
//
//        return cartDetailDTOList;
//    }

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
