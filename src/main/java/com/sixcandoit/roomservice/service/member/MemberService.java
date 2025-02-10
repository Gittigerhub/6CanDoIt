package com.sixcandoit.roomservice.service.member;

import com.sixcandoit.roomservice.constant.Level;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberEntity register(MemberDTO memberDTO) {
        Optional<MemberEntity> user = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
        if (user.isPresent()) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }

        String password = passwordEncoder.encode(memberDTO.getPassword());

        MemberEntity memberEntity = modelMapper.map(memberDTO, MemberEntity.class);
        memberEntity.setPassword(password);
        memberEntity.setLevel(Level.MEMBER);

        return memberRepository.save(memberEntity);
    }

    public MemberEntity modify(MemberDTO memberDTO) {
        Optional<MemberEntity> user = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());

        if (user.isPresent()) {
            String password = passwordEncoder.encode(memberDTO.getPassword());
            MemberEntity memberEntity = modelMapper.map(memberDTO, MemberEntity.class);

            memberEntity.setMemberEmail(user.get().getMemberEmail());
            memberEntity.setPassword(password);
            memberEntity.setLevel(user.get().getLevel());

            return memberRepository.save(memberEntity);
        }

        return null;
    }

    public void delete(String memberEmail) {
        memberRepository.deleteByMemberEmail(memberEmail);
    }

    public MemberDTO read(String memberEmail) {
        Optional<MemberEntity> user = memberRepository.findByMemberEmail(memberEmail);

        MemberDTO memberDTO = modelMapper.map(user, MemberDTO.class);

        return memberDTO;
    }

}
