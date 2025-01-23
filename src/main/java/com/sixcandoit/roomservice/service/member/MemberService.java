package com.sixcandoit.roomservice.service.member;

import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

//Gmail에서 이메일 전송하도록 설정
//이메일 비밀키 발급
//임시 비밀번호를 이메일로 발송
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService { //사용자가 로그인정보 변경
    private final MemberRepository memberRepository; //SQL처리
    private final ModelMapper modelMapper; //변환
    private final PasswordEncoder passwordEncoder; //비밀번호 암호화
    private final EmailService emailService; //이메일로 임시번호 발급

    @Override
    public UserDetails loadUserByUsername(String memberEmail)
            throws UsernameNotFoundException { //입력받은 아이디가 없으면 예외처리
        //사용자아이디로 조회해서
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(memberEmail);

        if (memberEntity.isPresent()) { //아이디가 존재하면
            MemberDTO memberDTO = modelMapper.map(memberEntity, MemberDTO.class);

            return memberDTO;
        } else { //존재하지 않으면 예외처리
            throw new UsernameNotFoundException(memberEmail);
        }


        //조회한 결과를 보안에 전달하면 보안에서 인증확인
    }

    //회원가입
    public void saveMember(MemberDTO memberDTO) {
        try { //서버가 멈추는 것을 예방
            Long totalCount = memberRepository.count(); //저장된 회원수를 읽어온다.
            Optional<MemberEntity> read = memberRepository.findByMemberEmail(memberDTO.getMemberEmail()); //아이디 조회(아이디 중복)

            if(read.isPresent()) { //이미 가입된 아이디이면
                throw new IllegalStateException("이미 가입된 회원입니다.");
            }

            String memberPwd = passwordEncoder.encode(memberDTO.getMemberPwd()); //비밀번호 암호화처리
            MemberEntity memberEntity = modelMapper.map(memberDTO, MemberEntity.class); //DTO->Entity 변환
            memberEntity.setMemberPwd(memberPwd); //암호화한 비밀번호를 다시 저장

        } catch(IllegalStateException e) { //상태오류(데이터베이스 처리 실패시)
            //e.getMessage() 오류메세지
            System.out.println("회원 가입을 실패하였습니다."+e.getMessage());
            throw e; //호출한 곳으로 돌아간다.(return)
        } catch(Exception e) { //비정상적인 처리(오류발생)
            System.out.println("예기치 않은 문제가 발생하였습니다."+e.getMessage());
            throw new RuntimeException("가입 중 오류가 발생하였습니다."); //사용자가 오류처리
        }
    }

    // 회원 정보 수정
    public void modifyMember(MemberEntity member){
        MemberEntity existingMember = memberRepository.findByIdx(member.getIdx())
                .orElseThrow(()->new IllegalArgumentException("해당 회원 정보를 찾을 수 없습니다."));
        existingMember.setMemberBirth(member.getMemberBirth());
        existingMember.setMemberGender(member.getMemberGender());
        existingMember.setMemberName(member.getMemberName());
        existingMember.setMemberPhone(member.getMemberPhone());
        existingMember.setMemberAddress(member.getMemberAddress());
        memberRepository.save(existingMember);
    }

    // 회원 ID로 회원 조회
    public MemberEntity findByIdx(Integer idx){
        return memberRepository.findByIdx(idx).orElse(null);
    }

    //개별조회(회원수정시->회원정보 읽기)-관리자 회원수정을 할 때
    public MemberDTO readMember(String memberEmail) {
        try { //서버가 멈추는 것을 예방
            Optional<MemberEntity> read = memberRepository.findByMemberEmail(memberEmail);
            if(read.isPresent()) { //검색한 회원이 존재하면
                MemberDTO memberDTO = modelMapper.map(read, MemberDTO.class);
                return memberDTO;
            }
            return null;
        } catch(IllegalStateException e) { //상태오류(데이터베이스 처리 실패시)
            //e.getMessage() 오류메세지
            System.out.println("회원 조회를 실패하였습니다."+e.getMessage());
            throw e; //호출한 곳으로 돌아간다.(return)
        } catch(Exception e) { //비정상적인 처리(오류발생)
            System.out.println("예기치 않은 문제가 발생하였습니다."+e.getMessage());
            throw new RuntimeException("조회 중 오류가 발생하였습니다."); //사용자가 오류처리
        }
    }

    //회원정보 수정
    //수정(비밀번호 입력, 이름, 전화번호, 주소, 새비밀번호)
    public void modifyMember(MemberDTO memberDTO) {
        try { //서버가 멈추는 것을 예방
            Optional<MemberEntity> read = memberRepository.findByMemberEmail(memberDTO.getMemberEmail()); //존재여부 검색
           /* if(read.isPresent()) { //회원이 존재하면 수정
                //비밀번호 확인을 통해 2차 검증
                String password = passwordEncoder.encode(memberDTO.getPassword());
                if(passwordEncoder.matches(password, read.get().getPassword())) { //비밀번호가 일치하면
                    MemberEntity memberEntity = modelMapper.map(memberDTO, MemberEntity.class);
                    memberEntity.setPassword(password);
                    memberRepository.save(memberEntity);
                } else {
                    throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
                }
            } else { //존재하지 않으면 오류처리
                throw new IllegalStateException("일치하는 회원이 없습니다.");
            }*/
            //if문이 많으면 적은부분으로 코딩한다.
            if(!read.isPresent()) { //회원이 존재하면 수정
                throw new IllegalStateException("일치하는 회원이 없습니다.");
            }
            String MemberPwd = passwordEncoder.encode(memberDTO.getMemberPwd());
            if(!passwordEncoder.matches(MemberPwd, read.get().getMemberPwd())) {
                throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
            }
            MemberEntity memberEntity = modelMapper.map(memberDTO, MemberEntity.class);
            memberEntity.setMemberPwd(MemberPwd);
            memberRepository.save(memberEntity);
        } catch(IllegalStateException e) { //상태오류(데이터베이스 처리 실패시)
            //e.getMessage() 오류메세지
            System.out.println("회원 수정을 실패하였습니다."+e.getMessage());
            throw e; //호출한 곳으로 돌아간다.(return)
        } catch(Exception e) { //비정상적인 처리(오류발생)
            System.out.println("예기치 않은 문제가 발생하였습니다."+e.getMessage());
            throw new RuntimeException("수정 중 오류가 발생하였습니다."); //사용자가 오류처리
        }
    }

    // 임시비밀번호 발급(아이디, 회원명, 전화번호를 입력받아서 일치하면 해당 메일(아이디)로 임시비밀번호를 전송)
    public void passwordSend(MemberDTO memberDTO) {
        try { //서버가 멈추는 것을 예방
            Optional<MemberEntity> read = memberRepository.findByMemberEmail(memberDTO.getMemberEmail()); //조회
            if (!read.isPresent()) {    //일치하는 회원이 없으면(!)
                throw new IllegalStateException("일치하는 회원이 존재하지 않습니다.");
            }
            if (!read.get().getMemberName().equals(memberDTO.getMemberName())) { //이름이 일치하지 않으면
                throw new IllegalStateException("회원 이름이 일치하지 않습니다.");
            }
            if (!read.get().getMemberPhone().equals(memberDTO.getMemberPhone())) { //전화번호가 일치하지 않으면
                throw new IllegalStateException("회원 전화번호가 일치하지 않습니다.");
            }
            String tempPassword = generateTempPassword(8);  //임시비밀번호 생성
            read.get().setMemberPwd(passwordEncoder.encode(tempPassword));   //임시비밀번호를 저장
            memberRepository.save(read.get());  //데이터베이스에 저장

            //임시비밀번호를 회원 이메일(아이디)로 전달
            String emailSubject = "임시비밀번호 발급";  //메일 제목
            String emailText = "안녕하세요."+read.get().getMemberName()+"님.\n" +
                    "요청하신 임시 비밀번호는 다음과 같습니다.\n" +
                    tempPassword+ "\n" +
                    "로그인 후 반드시 비밀번호를 변경해 주십시오";  //본문 내용

            emailService.sendEmail(read.get().getMemberEmail(), emailSubject, emailText);    //메일 전송
        } catch(IllegalStateException e) { //상태오류(데이터베이스 처리 실패시)
            //e.getMessage() 오류메세지
            System.out.println("회원 가입을 실패하였습니다."+e.getMessage());
            throw e; //호출한 곳으로 돌아간다.(return)
        } catch(Exception e) { //비정상적인 처리(오류발생)
            System.out.println("예기치 않은 문제가 발생하였습니다."+e.getMessage());
            throw new RuntimeException("가입 중 오류가 발생하였습니다."); //사용자가 오류처리
        }
    }

    // 비밀번호 생성기(입력한 자리수만큼 임시비밀번호를 생성)
    private String generateTempPassword(int length){
        //비밀번호에 사용할 문자열
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        try {
            for (int i = 0; i < length; i++) {
                int index = random.nextInt(chars.length());     //0~문자열개수 사이의 정수형 난수 발생
                sb.append(chars.charAt(index)); //해당 위치의 문자열을 추가
            }
            return sb.toString();   //생성한 비밀번호 전달
        } catch(Exception e) {
            System.err.println("임시 비밀번호 생성 실패!!!"); //콘솔에 오류메세지 출력
            throw new RuntimeException("임시 비밀번호 생성을 실패하였습니다."); //호출한 메소드에 전달한 오류
        }
    }
}
