package com.sixcandoit.roomservice.service.admin;

import com.sixcandoit.roomservice.config.CustomUserDetails;
import com.sixcandoit.roomservice.constant.Level;
import com.sixcandoit.roomservice.dto.admin.AdminDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.admin.AdminRepository;
import com.sixcandoit.roomservice.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AdminService {

    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 이메일 중복 체크 메서드
    public boolean checkEmailExistence(String email) {
        return adminRepository.existsByAdminEmail(email);
    }

    // 관리자 회원 가입
    public AdminEntity register(AdminDTO adminDTO) {
        Optional<AdminEntity> user = adminRepository.findByAdminEmail(adminDTO.getAdminEmail());

        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }
        System.out.println(adminDTO.getAdminEmail());
        System.out.println(adminDTO.getPassword());
        System.out.println(adminDTO.toString());

        String password = passwordEncoder.encode(adminDTO.getPassword());
        AdminEntity adminEntity = modelMapper.map(adminDTO, AdminEntity.class);
        adminEntity.setPassword(password);
        adminEntity.setLevel(Level.ADMIN);

        return adminRepository.save(adminEntity);
    }

    // 회원 정보 수정
    public AdminEntity modify(AdminDTO adminDTO) {
        Optional<AdminEntity> user = adminRepository.findByAdminEmail(adminDTO.getAdminEmail());

        if (user.isPresent()) {
            String password = passwordEncoder.encode(adminDTO.getPassword());

            AdminEntity adminEntity = modelMapper.map(adminDTO, AdminEntity.class);

            adminEntity.setAdminEmail(adminDTO.getAdminEmail());
            adminEntity.setPassword(password);
            adminEntity.setLevel(user.get().getLevel());

            return adminRepository.save(adminEntity);
        }
        return null;
    }

    // 회원 탈퇴
    public void delete(String AdminEmail) {
        adminRepository.findByAdminEmail(AdminEmail);
    }

    // 회원 읽기
    public AdminDTO read(String AdminEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String currentUser = authentication.getName();
        System.out.println("서비스에서 아이디 조회 : " + currentUser);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) principal;

        } else {
            // UserDetails가 CustomUserDetails가 아닌 경우에 대한 처리
            return null;
        }

        Optional<AdminEntity> user = adminRepository.findByAdminEmail(AdminEmail);

        AdminDTO adminDTO = modelMapper.map(user, AdminDTO.class);

        return adminDTO;
    }

    //임시비밀번호 발급(아이디, 회원명, 전화번호를 입력받아서 일치하면 해당 메일(아이디)로 임시비밀번호를 전송)
    public void passwordSend(AdminDTO adminDTO){
        try { //서버가 멈추는 것을 예방
            Optional<AdminEntity> read = adminRepository.findByAdminEmail(adminDTO.getAdminEmail()); //조회
            if (!read.isPresent()) { //일치하는 회원이 없으면
                throw new IllegalStateException("일치하는 회원이 존재하지 않습니다.");
            }
            if (!read.get().getAdminName().equals(adminDTO.getAdminName())){ //이름이 일치하지 않으면
                throw new IllegalStateException("회원이름이 일치하지 않습니다.");
            }
//            if (!read.get().getMemberPhone().equals(memberDTO.getMemberPhone())){ //전화번호가 일치하지 않으면
//                throw new IllegalStateException("전화번호가 일치하지 않습니다.");
//            }

            String tempPassword = generateTempPassword(8); //임시비밀번호 생성
            read.get().setPassword(passwordEncoder.encode(tempPassword)); //임시비밀번호를 저장
            adminRepository.save(read.get()); //데이터베이스에 저장

            //임시비밀번호를 회원 이메일(아이디)로 전달
            String emailSubject = "임시비밀번호 발급"; //메일제목
            String emailText = "안녕하세요"+read.get().getAdminName()+"님.\n"+
                    "요청하신 임시 비밀번호는 다음과 같습니다.\n"+
                    tempPassword+"\n"+
                    "로그인 후 반드시 비밀번호를 변경해 주십시오."; //본문내용

            emailService.sendEmail(read.get().getAdminEmail(), emailSubject, emailText); //메일전송

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
