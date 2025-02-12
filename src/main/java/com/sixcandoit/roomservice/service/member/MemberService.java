package com.sixcandoit.roomservice.service.member;

import com.sixcandoit.roomservice.constant.Level;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; //이메일로 임시번호 발급

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

    //임시비밀번호 발급(아이디, 회원명, 전화번호를 입력받아서 일치하면 해당 메일(아이디)로 임시비밀번호를 전송)
    public void passwordSend(MemberDTO memberDTO){
        try { //서버가 멈추는 것을 예방
            Optional<MemberEntity> read = memberRepository.findByMemberEmail(memberDTO.getMemberEmail()); //조회
            if (!read.isPresent()) { //일치하는 회원이 없으면
                throw new IllegalStateException("일치하는 회원이 존재하지 않습니다.");
            }
            if (!read.get().getMemberName().equals(memberDTO.getMemberName())){ //이름이 일치하지 않으면
                throw new IllegalStateException("회원이름이 일치하지 않습니다.");
            }
//            if (!read.get().getMemberPhone().equals(memberDTO.getMemberPhone())){ //전화번호가 일치하지 않으면
//                throw new IllegalStateException("전화번호가 일치하지 않습니다.");
//            }

            String tempPassword = generateTempPassword(8); //임시비밀번호 생성
            read.get().setPassword(passwordEncoder.encode(tempPassword)); //임시비밀번호를 저장
            memberRepository.save(read.get()); //데이터베이스에 저장

            //임시비밀번호를 회원 이메일(아이디)로 전달
            String emailSubject = "임시비밀번호 발급"; //메일제목
            String emailText = "안녕하세요 "+read.get().getMemberName()+"님.\n"+
                    "요청하신 임시 비밀번호는 다음과 같습니다.\n"+
                    tempPassword+"\n"+
                    "로그인 후 반드시 비밀번호를 변경해 주십시오."; //본문내용

            emailService.sendEmail(read.get().getMemberEmail(), emailSubject, emailText); //메일전송

        }catch (IllegalStateException e){ //상태오류(데이터베이스 처리 실패시)
            //e.getMessage() 오류메세지
            System.out.println("회원 가입을 실패하였습니다."+e.getMessage());
            throw e; //호출한 곳으로 돌아간다.(return)
        }catch (Exception e){ //비정상적인 처리(오류발생)
            System.out.println("예기치 않은 문제가 발생하였습니다."+e.getMessage());
            throw new RuntimeException("가입 중 오류가 발생하였습니다."); //사용자가 오류처리
        }
    }

    //비밀번호 생성기(입력한 자리수만큼 임시비밀번호를 생성)
    private String generateTempPassword(int length){
        //비밀번호에 사용할 문자열
        final String chars = "ABCDEFGHIJKLNMOPQRSTUVWXYZabcdefghijklnmopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        try {
            for (int i = 0; i < length; i++){
                int index = random.nextInt(chars.length()); //0~문자열개수 사이의 정수형 난수발생
                sb.append(chars.charAt(index)); //해당위치의 문자열을 추가
            }
            return sb.toString(); //생성한 비밀번호 전달
        }catch (Exception e){
            System.out.println("임시 비밀번호 생성 실패!!!"); //콘솔에 오류메세지 출력
            throw new RuntimeException("임시 비밀번호 생성을 실패하였습니다."); //호출한 메소드에 전달한 오류
        }
    }
}

