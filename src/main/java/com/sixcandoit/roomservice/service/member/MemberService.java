package com.sixcandoit.roomservice.service.member;


import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    //UserDetailsService 추상클래스로 구성되어있다.
    //오버라이드로 특정 메소드를 사용자가 원하는 내용으로 변경해서 적용
    //throws UsernameNotFoundException 사용자 이름이 없으면 예외처리 발생
    @Override
    public UserDetails loadUserByUsername(String memberEmail) throws UsernameNotFoundException {
        //입력받은 userid로 해당 사용자가 존재하는지를 확인
        Optional<MemberEntity> memberEntity = memberRepository.findBymemberEmail(memberEmail);

        if (memberEntity.isPresent()) {  //userid가 존재하면
            //데이터베이스에 있는 회원정보를 가지고 로그인 처리
            return User.withUsername(memberEntity.get().getMemberEmail())
                    .password(memberEntity.get().getMemberPwd())
                    .roles(memberEntity.get().getMemberType())
                    .build();
        } else {    //userid가 존재하지 않으면, 오류 발생
            throw new UsernameNotFoundException(memberEmail+"이 존재하지 않습니다.");
        }
    }

}
