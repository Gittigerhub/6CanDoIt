package com.sixcandoit.roomservice.service.member;

import com.sixcandoit.roomservice.config.CustomUserDetails;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import groovy.util.logging.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Log4j2
public class MemberLoginService implements UserDetailsService { //사용자가 로그인정보 변경
    @Autowired
    private MemberRepository memberRepository;

    //보안인증을 통한 로그인처리(.usernameParameter("userid") =>userid)
    @Override
    public UserDetails loadUserByUsername(String memberEmail) {
        System.out.println("사용자 로그인");
        //받아온 userid가 일반회원에 존재하는지 조회
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(memberEmail);
        if (memberEntity.isPresent()) { //일반회원에 존재하면

            MemberEntity member = memberRepository.findByMemberEmail(memberEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Member not found: " + memberEmail));
            return new CustomUserDetails(member); // Member 객체 반환

            //아이디, 비밀번호, 권한을 가져가서 로그인를 처리
//            return User.withUsername(memberEntity.get().getMemberEmail())
//                    .password(memberEntity.get().getPassword())
//                    .roles(memberEntity.get().getLevel().name())
//                    .build();
        }

        //일반회원 및 관리자에 존재하지 않으면 오류발생(Console)
        throw new UsernameNotFoundException("알 수 없는 아이디 : "+ memberEmail);
    }
}
