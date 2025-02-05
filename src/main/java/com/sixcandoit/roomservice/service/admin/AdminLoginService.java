package com.sixcandoit.roomservice.service.admin;

import com.sixcandoit.roomservice.repository.admin.AdminRepository;
import com.sixcandoit.roomservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminLoginService  {
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
//
//    @Override
//    public UserDetails loadUserByUsername(String adminEmail)
//            throws UsernameNotFoundException { //입력받은 아이디가 없으면 예외처리
//        //사용자아이디로 조회해서
//        Optional<AdminEntity> adminEntity = adminRepository.findByAdminEmail(adminEmail);
//
//        if (adminEntity.isPresent()) { //아이디가 존재하면
//            AdminLoginDTO adminLoginDTO = modelMapper.map(adminEntity, AdminLoginDTO.class);
//
//            return adminLoginDTO;
//        } else { //존재하지 않으면 예외처리
//            throw new UsernameNotFoundException(adminEmail);
//        }
//
//
//        //조회한 결과를 보안에 전달하면 보안에서 인증확인
//    }
//
//    //회원가입
//    public void saveAdmin(AdminLoginDTO adminLoginDTO) {
//        try { //서버가 멈추는 것을 예방
//            Long totalCount = adminRepository.count(); //저장된 회원수를 읽어온다.
//            Optional<AdminEntity> read = adminRepository.findByAdminEmail(adminLoginDTO.getAdminEmail()); //아이디 조회(아이디 중복)
//
//            if(read.isPresent()) { //이미 가입된 아이디이면
//                throw new IllegalStateException("이미 가입된 회원입니다.");
//            }
//
//            String adminPwd = passwordEncoder.encode(adminLoginDTO.getAdminPwd()); //비밀번호 암호화처리
//            AdminEntity adminEntity = modelMapper.map(adminLoginDTO, AdminEntity.class); //DTO->Entity 변환
//            adminEntity.setAdminPwd(adminPwd); //암호화한 비밀번호를 다시 저장
//
//        } catch(IllegalStateException e) { //상태오류(데이터베이스 처리 실패시)
//            //e.getMessage() 오류메세지
//            System.out.println("회원 가입을 실패하였습니다."+e.getMessage());
//            throw e; //호출한 곳으로 돌아간다.(return)
//        } catch(Exception e) { //비정상적인 처리(오류발생)
//            System.out.println("예기치 않은 문제가 발생하였습니다."+e.getMessage());
//            throw new RuntimeException("가입 중 오류가 발생하였습니다."); //사용자가 오류처리
//        }
//    }
//
//    //개별조회(회원수정시->회원정보 읽기)-관리자 회원수정을 할 때
//    public AdminLoginDTO readAdmin(String adminEmail) {
//        try { //서버가 멈추는 것을 예방
//            Optional<AdminEntity> read = adminRepository.findByAdminEmail(adminEmail);
//            if(read.isPresent()) { //검색한 회원이 존재하면
//                AdminLoginDTO adminLoginDTO = modelMapper.map(read, AdminLoginDTO.class);
//                return adminLoginDTO;
//            }
//            return null;
//        } catch(IllegalStateException e) { //상태오류(데이터베이스 처리 실패시)
//            //e.getMessage() 오류메세지
//            System.out.println("회원 조회를 실패하였습니다."+e.getMessage());
//            throw e; //호출한 곳으로 돌아간다.(return)
//        } catch(Exception e) { //비정상적인 처리(오류발생)
//            System.out.println("예기치 않은 문제가 발생하였습니다."+e.getMessage());
//            throw new RuntimeException("조회 중 오류가 발생하였습니다."); //사용자가 오류처리
//        }
//    }
//
//    //회원정보 수정
//    //수정(비밀번호 입력, 이름, 전화번호, 주소, 새비밀번호)
//    public void modifyAdmin(AdminLoginDTO adminLoginDTO) {
//        try { //서버가 멈추는 것을 예방
//            Optional<AdminEntity> read = adminRepository.findByAdminEmail(adminLoginDTO.getAdminEmail()); //존재여부 검색
//           /* if(read.isPresent()) { //회원이 존재하면 수정
//                //비밀번호 확인을 통해 2차 검증
//                String password = passwordEncoder.encode(memberDTO.getPassword());
//                if(passwordEncoder.matches(password, read.get().getPassword())) { //비밀번호가 일치하면
//                    MemberEntity memberEntity = modelMapper.map(memberDTO, MemberEntity.class);
//                    memberEntity.setPassword(password);
//                    memberRepository.save(memberEntity);
//                } else {
//                    throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
//                }
//            } else { //존재하지 않으면 오류처리
//                throw new IllegalStateException("일치하는 회원이 없습니다.");
//            }*/
//            //if문이 많으면 적은부분으로 코딩한다.
//            if(!read.isPresent()) { //회원이 존재하면 수정
//                throw new IllegalStateException("일치하는 회원이 없습니다.");
//            }
//            String adminPwd = passwordEncoder.encode(adminLoginDTO.getAdminPwd());
//            if(!passwordEncoder.matches(adminPwd, read.get().getAdminPwd())) {
//                throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
//            }
//            AdminEntity adminEntity = modelMapper.map(adminLoginDTO, AdminEntity.class);
//            adminEntity.setAdminPwd(adminPwd);
//            adminRepository.save(adminEntity);
//        } catch(IllegalStateException e) { //상태오류(데이터베이스 처리 실패시)
//            //e.getMessage() 오류메세지
//            System.out.println("회원 수정을 실패하였습니다."+e.getMessage());
//            throw e; //호출한 곳으로 돌아간다.(return)
//        } catch(Exception e) { //비정상적인 처리(오류발생)
//            System.out.println("예기치 않은 문제가 발생하였습니다."+e.getMessage());
//            throw new RuntimeException("수정 중 오류가 발생하였습니다."); //사용자가 오류처리
//        }
//    }
//
//    // 임시비밀번호 발급(아이디, 회원명, 전화번호를 입력받아서 일치하면 해당 메일(아이디)로 임시비밀번호를 전송)
//    public void passwordSend(AdminLoginDTO adminLoginDTO) {
//        try { //서버가 멈추는 것을 예방
//            Optional<AdminEntity> read = adminRepository.findByAdminEmail(adminLoginDTO.getAdminEmail()); //조회
//            if (!read.isPresent()) {    //일치하는 회원이 없으면(!)
//                throw new IllegalStateException("일치하는 회원이 존재하지 않습니다.");
//            }
//            if (!read.get().getAdminName().equals(adminLoginDTO.getAdminName())) { //이름이 일치하지 않으면
//                throw new IllegalStateException("회원 이름이 일치하지 않습니다.");
//            }
//            if (!read.get().getAdminPhone().equals(adminLoginDTO.getAdminPhone())) { //전화번호가 일치하지 않으면
//                throw new IllegalStateException("회원 전화번호가 일치하지 않습니다.");
//            }
//            String tempPassword = generateTempPassword(8);  //임시비밀번호 생성
//            read.get().setAdminPwd(passwordEncoder.encode(tempPassword));   //임시비밀번호를 저장
//            adminRepository.save(read.get());  //데이터베이스에 저장
//
//            //임시비밀번호를 회원 이메일(아이디)로 전달
//            String emailSubject = "임시비밀번호 발급";  //메일 제목
//            String emailText = "안녕하세요."+read.get().getAdminName()+"님.\n" +
//                    "요청하신 임시 비밀번호는 다음과 같습니다.\n" +
//                    tempPassword+ "\n" +
//                    "로그인 후 반드시 비밀번호를 변경해 주십시오";  //본문 내용
//
//            emailService.sendEmail(read.get().getAdminEmail(), emailSubject, emailText);    //메일 전송
//        } catch(IllegalStateException e) { //상태오류(데이터베이스 처리 실패시)
//            //e.getMessage() 오류메세지
//            System.out.println("회원 가입을 실패하였습니다."+e.getMessage());
//            throw e; //호출한 곳으로 돌아간다.(return)
//        } catch(Exception e) { //비정상적인 처리(오류발생)
//            System.out.println("예기치 않은 문제가 발생하였습니다."+e.getMessage());
//            throw new RuntimeException("가입 중 오류가 발생하였습니다."); //사용자가 오류처리
//        }
//    }
//
//    // 비밀번호 생성기(입력한 자리수만큼 임시비밀번호를 생성)
//    private String generateTempPassword(int length){
//        //비밀번호에 사용할 문자열
//        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//        SecureRandom random = new SecureRandom();
//        StringBuilder sb = new StringBuilder(length);
//        try {
//            for (int i = 0; i < length; i++) {
//                int index = random.nextInt(chars.length());     //0~문자열개수 사이의 정수형 난수 발생
//                sb.append(chars.charAt(index)); //해당 위치의 문자열을 추가
//            }
//            return sb.toString();   //생성한 비밀번호 전달
//        } catch(Exception e) {
//            System.err.println("임시 비밀번호 생성 실패!!!"); //콘솔에 오류메세지 출력
//            throw new RuntimeException("임시 비밀번호 생성을 실패하였습니다."); //호출한 메소드에 전달한 오류
//        }
//    }
}
