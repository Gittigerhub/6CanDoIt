package com.sixcandoit.roomservice.service.orders;

import com.sixcandoit.roomservice.dto.orders.CartMenuDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.orders.CartMenuEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.repository.orders.CartMenuRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional

public class CartMenuService {

    public final CartMenuRepository cartMenuRepository;
    public final MemberRepository memberRepository;

    // 카트의 pk, MenuIdx의 기본키로 cartMenu을 찾는다. 내 카트에 담긴 몇번 아이템
    // 내 카트에 담긴 아이템 = cartMenu

    public Integer updatecartMenuIdx(CartMenuDTO cartMenuDTO, String memberEmail) throws Exception{
        //내 카트가 맞는지 확인
        Optional<MemberEntity> memberEntity =
                memberRepository.findByMemberEmail(memberEmail);
        //카드 아이디를 받아온 후
        //로그인 한 회원정보와 비교
        //카트 아이템 아이디만 남도록

        //컨트롤러에서 받은 CartMenuDTO에 CartMenuIdx로

        CartMenuEntity cartMenuEntity =
                cartMenuRepository.findById(cartMenuDTO.getMenuidx()).orElseThrow(EntityNotFoundException::new);


        if(cartMenuEntity.getCartEntity() != null && memberEntity != null && cartMenuEntity.getCartEntity().getMemberJoin().getIdx() != memberEntity.get().getIdx()){
            throw new Exception();
        }
        cartMenuEntity.setCount(cartMenuDTO.getCount());
        return cartMenuEntity.getIdx();
    }

}
