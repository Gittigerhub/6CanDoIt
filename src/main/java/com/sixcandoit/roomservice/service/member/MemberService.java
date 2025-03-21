package com.sixcandoit.roomservice.service.member;

import com.sixcandoit.roomservice.constant.Level;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.orders.OrdersEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.repository.orders.OrdersRepository;
import com.sixcandoit.roomservice.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; //이메일로 임시번호 발급

    // 이메일 중복 체크 메서드
    public boolean checkEmailExistence(String email) {
        return memberRepository.existsByMemberEmail(email);
    }

    // 연락처 중복 체크 메서드
    public boolean checkPhoneExistence(String phone) {
        return memberRepository.existsByMemberPhone(phone); // 연락처 중복 여부 확인
    }


    // 일반 사용자 회원 가입
    public MemberEntity register(MemberDTO memberDTO) {
        // 이메일 중복 체크
        if (checkEmailExistence(memberDTO.getMemberEmail())) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }

        String password = passwordEncoder.encode(memberDTO.getPassword());

        MemberEntity memberEntity = modelMapper.map(memberDTO, MemberEntity.class);
        memberEntity.setPassword(password);
        memberEntity.setLevel(Level.MEMBER);
        memberEntity.setMemberType("일반"); // 일반 회원가입의 경우 memberType을 '일반'으로 설정

        return memberRepository.save(memberEntity);
    }

    // 비밀번호 확인
    public boolean verifyPassword(String inputPassword, String memberEmail) {
        log.info("비밀번호 검증 시작 - 이메일: " + memberEmail);
        
        // DB에서 회원 정보를 조회 (Optional<MemberEntity> 반환)
        Optional<MemberEntity> optionalMember = memberRepository.findByMemberEmail(memberEmail);

        if (optionalMember.isPresent()) {
            // 회원이 존재하면, 비밀번호 비교
            MemberEntity memberEntity = optionalMember.get(); // MemberEntity 객체 추출
            log.info("회원 정보 조회 성공 - 이메일: " + memberEmail);
            
            boolean isMatch = passwordEncoder.matches(inputPassword, memberEntity.getPassword());
            log.info("비밀번호 검증 결과 - 이메일: " + memberEmail + ", 일치여부: " + isMatch);

            return isMatch;
        } else {
            // 회원이 없으면 false 반환
            log.error("회원 정보 조회 실패 - 이메일: " + memberEmail);
            return false;
        }
    }

    // 회원 정보 수정
    public MemberEntity modify(MemberDTO memberDTO) {

        log.info("멤버 이메일은?"+memberDTO.getMemberEmail());
        Optional<MemberEntity> member = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
        log.info("가지고 왔나요 ? "+member);

        if (member.isPresent()) {
            log.info("회원 정보를 수정하자");

            // 기존 회원 정보 가져오기
            MemberEntity memberEntity = member.get();

//            // 비밀번호 인코딩 후 변경
//            String password = passwordEncoder.encode(memberDTO.getPassword());
//            memberEntity.setPassword(password);

            // 다른 필드 업데이트
//            memberEntity.setLevel(memberDTO.getLevel());
            memberEntity.setMemberName(memberDTO.getMemberName());
            memberEntity.setMemberBirth(memberDTO.getMemberBirth());
            memberEntity.setMemberGender(memberDTO.getMemberGender());
            memberEntity.setMemberPhone(memberDTO.getMemberPhone());
            memberEntity.setMemberAddress(memberDTO.getMemberAddress());
            memberEntity.setAlramOrder(memberDTO.getAlramOrder());


//            String password = passwordEncoder.encode(memberDTO.getPassword());
//            MemberEntity memberEntity = modelMapper.map(memberDTO, MemberEntity.class);
//
//            memberEntity.setMemberEmail(memberDTO.getMemberEmail());
//            memberEntity.setPassword(password);
//            memberEntity.setLevel(memberDTO.getLevel());

            return memberRepository.save(memberEntity);
        }

        return null;
    }

    // 비밀번호 변경
    public boolean changePassword(String memberEmail, String currentPassword, String newPassword) {
        Optional<MemberEntity> memberOpt = memberRepository.findByMemberEmail(memberEmail);

        if (memberOpt.isPresent()) {
            MemberEntity member = memberOpt.get();

            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
                return false; // 현재 비밀번호가 틀림
            }

            // 새 비밀번호 암호화 후 저장
            member.setPassword(passwordEncoder.encode(newPassword));
            memberRepository.save(member);
            return true;
        }

        return false;
    }


    // 회원 탈퇴
    public void delete(String memberEmail) {
        memberRepository.deleteByMemberEmail(memberEmail);
    }

    // 회원 읽기
    public MemberDTO read(String memberEmail) {
        Optional<MemberEntity> user = memberRepository.findByMemberEmail(memberEmail);

        MemberDTO memberDTO = modelMapper.map(user, MemberDTO.class);

        return memberDTO;
    }

    //임시비밀번호 발급(아이디, 회원명을 입력받아서 일치하면 해당 메일(아이디)로 임시비밀번호를 전송)
    public boolean passwordSend(MemberDTO memberDTO) {
        try { //서버가 멈추는 것을 예방
            Optional<MemberEntity> read = memberRepository.findByMemberEmail(memberDTO.getMemberEmail()); //조회
            if (!read.isPresent()) { //일치하는 회원이 없으면
                log.info("에러1");
                throw new IllegalStateException("일치하는 회원이 존재하지 않습니다.");
            }
            if (!read.get().getMemberName().equals(memberDTO.getMemberName())) { //이름이 일치하지 않으면
                log.info("에러2");
                throw new IllegalStateException("회원이름이 일치하지 않습니다.");
            }
            
            String tempPassword = generateTempPassword(8); //임시비밀번호 생성
            read.get().setPassword(passwordEncoder.encode(tempPassword)); //임시비밀번호를 저장
            memberRepository.save(read.get()); //데이터베이스에 저장

            String message = emailService.getTempEmailHTML(tempPassword, 2);
            String to = read.get().getMemberEmail();
            String subject = "임시 비밀번호 발급";

            emailService.sendEmail(to, subject, message); //메일전송
            return true;

        } catch (IllegalStateException e) { //상태오류(데이터베이스 처리 실패시)
            //e.getMessage() 오류메세지
            System.out.println("회원 가입을 실패하였습니다." + e.getMessage());
            return false;
        } catch (Exception e) { //비정상적인 처리(오류발생)
            System.out.println("예기치 않은 문제가 발생하였습니다." + e.getMessage());
            return false;
        }
    }

    //비밀번호 생성기(입력한 자리수만큼 임시비밀번호를 생성)
    private String generateTempPassword(int length) {
        //비밀번호에 사용할 문자열
        final String chars = "ABCDEFGHIJKLNMOPQRSTUVWXYZabcdefghijklnmopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        try {
            for (int i = 0; i < length; i++) {
                int index = random.nextInt(chars.length()); //0~문자열개수 사이의 정수형 난수발생
                sb.append(chars.charAt(index)); //해당위치의 문자열을 추가
            }
            return sb.toString(); //생성한 비밀번호 전달
        } catch (Exception e) {
            System.out.println("임시 비밀번호 생성 실패!!!"); //콘솔에 오류메세지 출력
            throw new RuntimeException("임시 비밀번호 생성을 실패하였습니다."); //호출한 메소드에 전달한 오류
        }
    }

    // 일반 사용자 삭제
    public boolean deleteMember(Integer idx) {
        try {
            memberRepository.deleteById(idx);
            return true;
        } catch (Exception e) {
            log.error("회원 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    // 일반 회원 전체 목록, 데이터를 화면에 출력
    // 페이지 번호를 받아 테이블의 해당 페이지의 데이터를 읽어와서 컨트롤러에 전달
    public Page<MemberDTO> memberList(Pageable page, String type, String keyword) {

        int currentPage = page.getPageNumber() - 1; // 화면의 페이지 번호를 db 페이지 번호로
        int pageLimit = 10; // 한 페이지를 구성하는 레코드 수

        // 지정된 내용으로 페이지 정보를 재생산, 정렬 내림차순 DESC
        Pageable pageable = PageRequest.of(currentPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "idx"));

        // 조회한 변수를 선언
        // type : 제목(1), 내용(2), 제목+내용(2), 답변만(4), 자주 묻는 질문(5), 전체(0)
        Page<MemberEntity> memberEntities;
        if (keyword != null) { // 검색어가 존재하면
            log.info("검색어가 존재하면...");
            if (type.equals("1")) { // type 분류 1, 이름으로 검색할 때
                log.info("이름으로 검색하는 중...");
                memberEntities = memberRepository.searchMemberName(keyword, pageable);
            } else if (type.equals("2")) { // type 분류 2, 이메일로 검색할 때
                log.info("이메일로 검색하는 중...");
                memberEntities = memberRepository.searchMemberEmail(keyword, pageable);
            } else if (type.equals("3")) { // type 분류 3, 핸드폰 번호로 검색할 때
                log.info("핸드폰 번호로 검색하는 중...");
                memberEntities = memberRepository.searchMemberPhone(keyword, pageable);
            } else { // 전체 검색 = 0
                log.info("전체 조회 검색 중...");
                memberEntities = memberRepository.searchMemberNameAndEmailAndPhone(keyword, pageable);
            }
        } else { // 검색어가 존재하지 않으면 모두 검색
            memberEntities = memberRepository.findAll(pageable);
        }

        // Entity를 DTO로 변환 후 저장
        Page<MemberDTO> memberDTOS = memberEntities.map(
                data -> modelMapper.map(data, MemberDTO.class));

        return memberDTOS;
    }

//        // 일반 회원 번호의 데이터를 화면에 출력
//        public MemberDTO memberRead(Integer idx){
//            Optional<MemberEntity> memberEntity = memberRepository.findByIdx(idx);
//
//            MemberDTO memberDTO = modelMapper.map(memberEntity, MemberDTO.class);
//
//            return memberDTO;
//        }

    // 마이페이지 주문내역 목록조회
    public Page<OrdersDTO> memberOrderList(String memberEmail, Pageable page) {

        try {
            // 1. 페이지정보를 재가공
            int currentPage = page.getPageNumber() - 1;
            int pageSize = 3;

            // 기본 키로 올림차순해서 페이지 조회
            Pageable pageable = PageRequest.of(currentPage, pageSize,
                    Sort.by(Sort.Direction.DESC, "idx"));

            // 2. 조회
            // 주문목록 조회
            Page<OrdersEntity> ordersEntities = ordersRepository.findOrdersEntity(memberEmail, pageable);

            if (ordersEntities == null || ordersEntities.isEmpty()) {
                System.out.println("ordersEntities가 null이거나 비어 있음!");
            }

            // 3. 조회한 결과를 HTML에서 사용할 DTO로 변환
            Page<OrdersDTO> ordersDTOS =
                    ordersEntities.map(entity -> {
                        System.out.println("변환 중: " + entity.toString());

                        OrdersDTO dto = new OrdersDTO();
                        try {
                            dto = modelMapper.map(entity, OrdersDTO.class);
                        } catch (Exception e) {
                            System.out.println("❌ modelMapper 변환 오류: " + e.getMessage());
                            e.printStackTrace();
                        }
                        return dto;
                    });

            // 4. 결과값을 전달
            return ordersDTOS;

        } catch (Exception e) {     //오류발생시 오류 처리
            throw new RuntimeException("조회 오류");
        }

    }

}

