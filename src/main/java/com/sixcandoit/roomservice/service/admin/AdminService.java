package com.sixcandoit.roomservice.service.admin;

import com.sixcandoit.roomservice.config.CustomUserDetails;
import com.sixcandoit.roomservice.constant.Level;
import com.sixcandoit.roomservice.dto.admin.AdminDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.admin.AdminRepository;
import com.sixcandoit.roomservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 연락처 중복 체크 메서드
    public boolean checkPhoneExistence(String phone) {
        return adminRepository.existsByAdminPhone(phone); // 연락처 중복 여부 확인
    }

    // 이메일 찾기 메서드
    public AdminEntity findByAdminEmail(String adminEmail) {
        System.out.println("입력된 이메일: " + adminEmail);
        return adminRepository.findByAdminEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 관리자가 존재하지 않습니다."));
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
        adminEntity.setLevel(Level.GUEST);

        return adminRepository.save(adminEntity);
    }

    // 비밀번호 확인
    public boolean verifyPassword(String inputPassword, String adminEmail) {
        // DB에서 회원 정보를 조회 (Optional<AdminEntity> 반환)
        Optional<AdminEntity> optionalAdmin = adminRepository.findByAdminEmail(adminEmail);
        log.info("DB에서 회원 정보 조회를 했니? " + optionalAdmin);

        if (optionalAdmin.isPresent()) {
            log.info("회원이 존재해요!!");
            // 회원이 존재하면, 비밀번호 비교
            AdminEntity adminEntity = optionalAdmin.get(); // AdminEntity 객체 추출
            boolean isMatch = passwordEncoder.matches(inputPassword, adminEntity.getPassword());

            // 로그 추가
            log.info("비밀번호 검증 결과: " + isMatch); // 비밀번호 일치 여부 확인

            return isMatch;
        } else {
            log.info("회원이 없는데요...?");
            // 회원이 없으면 false 반환
            return false;
        }

    }

    // 회원 정보 수정
    public AdminEntity modify(AdminDTO adminDTO) {

        log.info("관리자 이메일은?"+adminDTO.getAdminEmail());
        Optional<AdminEntity> admin = adminRepository.findByAdminEmail(adminDTO.getAdminEmail());
        log.info("가지고 왔나요 ? " + admin);

        if (admin.isPresent()) {
            log.info("회원 정보를 수정해보자");

            // 기존 회원 정보 가져오기
            AdminEntity adminEntity = admin.get();
            log.info("기존 회원정보를 가지고 왔나요? " +  adminEntity);

//            adminEntity.setAdminEmail(adminDTO.getAdminEmail());
            adminEntity.setAdminName(adminDTO.getAdminName());
            adminEntity.setAdminPhone(adminDTO.getAdminPhone());

            return adminRepository.save(adminEntity);

        }
        log.info("null이래 ....");
        return null;
    }

    // 비밀번호 변경
    public boolean changePassword(String adminEmail, String currentPassword, String newPassword) {
        Optional<AdminEntity> adminOpt = adminRepository.findByAdminEmail(adminEmail);
        log.info("비번 변경을 합시다 1차 확인 해봐" + adminOpt);

        if (adminOpt.isPresent()) {
            AdminEntity admin = adminOpt.get();
            log.info("가지고 왔니? " + admin);

            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(currentPassword, admin.getPassword())) {
                log.info("현재 비밀번호가 틀렸어요 !! ");
                return false; // 현재 비밀번호가 틀림
            }

            // 새 비밀번호 암호화 후 저장
            admin.setPassword(passwordEncoder.encode(newPassword));
            adminRepository.save(admin);
            log.info("비밀번호 변경을 했어요~");
            return true;
        }

        log.info("비밀번호 변경 실패~~~");
        return false;
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
    public void passwordSend(AdminDTO adminDTO) {
        try { //서버가 멈추는 것을 예방
            Optional<AdminEntity> read = adminRepository.findByAdminEmail(adminDTO.getAdminEmail()); //조회
            if (!read.isPresent()) { //일치하는 회원이 없으면
                throw new IllegalStateException("일치하는 회원이 존재하지 않습니다.");
            }
            if (!read.get().getAdminName().equals(adminDTO.getAdminName())) { //이름이 일치하지 않으면
                throw new IllegalStateException("회원이름이 일치하지 않습니다.");
            }
//            if (!read.get().getMemberPhone().equals(memberDTO.getMemberPhone())){ //전화번호가 일치하지 않으면
//                throw new IllegalStateException("전화번호가 일치하지 않습니다.");
//            }
            else {

                String tempPassword = generateTempPassword(8); //임시비밀번호 생성
                read.get().setPassword(passwordEncoder.encode(tempPassword)); //임시비밀번호를 저장
                adminRepository.save(read.get()); //데이터베이스에 저장

//            //임시비밀번호를 회원 이메일(아이디)로 전달
//            String emailSubject = "임시비밀번호 발급"; //메일제목
//            String emailText = "안녕하세요"+read.get().getAdminName()+"님.\n"+
//                    "요청하신 임시 비밀번호는 다음과 같습니다.\n"+
//                    tempPassword+"\n"+
//                    "로그인 후 반드시 비밀번호를 변경해 주십시오."; //본문내용

                String message = emailService.getTempEmailHTML(tempPassword, 1);
                String to = read.get().getAdminEmail();
                String subject = "임시 비밀번호 발급";
                log.info(message);


                emailService.sendEmail(to, subject, message);
            }

        } catch (IllegalStateException e) { //상태오류(데이터베이스 처리 실패시)
            //e.getMessage() 오류메세지
            System.out.println("회원 가입을 실패하였습니다." + e.getMessage());
            throw e; //호출한 곳으로 돌아간다.(return)
        } catch (Exception e) { //비정상적인 처리(오류발생)
            System.out.println("예기치 않은 문제가 발생하였습니다." + e.getMessage());
            throw new RuntimeException("가입 중 오류가 발생하였습니다."); //사용자가 오류처리
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

    // 관리자 회원 삭제
    public boolean deleteAdmin(Integer idx) {
        try {
            adminRepository.deleteById(idx);
            return true;
        } catch (Exception e) {
            log.error("관리자 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    // 관리자 회원 전체 목록, 데이터를 화면에 출력
    // 페이지 번호를 받아 테이블의 해당 페이지의 데이터를 읽어와서 컨트롤러에 전달
    public Page<AdminDTO> adminList(Pageable page, String type, String keyword, Level level) {

        int currentPage = page.getPageNumber() - 1; // 화면의 페이지 번호를 db 페이지 번호로
        int pageLimit = 10; // 한 페이지를 구성하는 레코드 수

        // 지정된 내용으로 페이지 정보를 재생산, 정렬 내림차순 DESC
        Pageable pageable = PageRequest.of(currentPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "idx"));

        // 조회한 변수를 선언
        // type : 제목(1), 내용(2), 제목+내용(2), 답변만(4), 자주 묻는 질문(5), 전체(0)
        Page<AdminEntity> adminEntities;
        if (keyword != null) { // 검색어가 존재하면
            log.info("검색어가 존재하면...");
            if (type.equals("1")) { // type 분류 1, 이름으로 검색할 때
                log.info("이름으로 검색하는 중...");
                adminEntities = adminRepository.searchAdminName(keyword, pageable);
            } else if (type.equals("2")) { // type 분류 2, 이메일로 검색할 때
                log.info("이메일로 검색하는 중...");
                adminEntities = adminRepository.searchAdminEmail(keyword, pageable);
            } else if (type.equals("3")) { // type 분류 3, 핸드폰 번호로 검색할 때
                log.info("핸드폰 번호로 검색하는 중...");
                adminEntities = adminRepository.searchAdminPhone(keyword, pageable);
            }else if (type.equals("4")){
                log.info("권한으로 검색하는 중...");
                adminEntities = adminRepository.searchAdminLevel(level, pageable);
            }else { // 전체 검색 = 0
                log.info("전체 조회 검색 중...");
                adminEntities = adminRepository.searchAdminNameAndEmailAndPhone(keyword, pageable);
            }
        } else { // 검색어가 존재하지 않으면 모두 검색
            adminEntities = adminRepository.findAll(pageable);
        }

        // Entity를 DTO로 변환 후 저장
        Page<AdminDTO> adminDTOS = adminEntities.map(
                data -> modelMapper.map(data, AdminDTO.class));

        return adminDTOS;
    }

    // 권한 변경
    public boolean updateAdminRole(String adminEmail, Level newLevel) {
        AdminEntity admin = findByAdminEmail(adminEmail);
        log.info("이메일을 찾아요" + admin.getAdminName());
        admin.setLevel(newLevel);
        log.info("새 권한을 찾아요" + newLevel);
        adminRepository.save(admin);
        log.info("저장 완료~" + admin);
        return true;
    }

    // 피라미드식 권한 변경
    public boolean canUpdateRole(Level currentAdminLevel, Level targetLevel) {
        log.info("당신의 권한은? " + currentAdminLevel);
        if (currentAdminLevel == Level.ADMIN) {
            return true; // ADMIN은 모든 레벨 변경 가능
        }
        if (currentAdminLevel == Level.HO) {
            return targetLevel == Level.BO || targetLevel == Level.GUEST; // BO와 GUEST 변경 가능
        }
        return false; // BO는 권한 변경 불가
    }

    public void authenticationCodeSend(String email){



    }



}
