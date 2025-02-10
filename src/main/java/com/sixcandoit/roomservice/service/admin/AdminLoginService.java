package com.sixcandoit.roomservice.service.admin;

import com.sixcandoit.roomservice.config.CustomUserDetails;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.repository.admin.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AdminLoginService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    //보안인증을 통한 로그인처리(.usernameParameter("userid") =>userid)
    @Override
    public UserDetails loadUserByUsername(String AdminEmail) {
        System.out.println("관리자 로그인");
//        //username에서 userid와 hotel 값을 분리
//        //엔터를 기준해서 단어를 분리해서 배열에 저장 [0]아이디, [1]호텔명
//        String[] parts = username.split("\n");
//        //trim() 양쪽에 존재하는 여백 제거
//        String actualUsername = parts[0].trim();
//        String hotel = parts[1].trim();

        //아이디와 호텔로 조회
        Optional<AdminEntity> adminEntity = adminRepository.findByAdminEmail(AdminEmail);
        System.out.println(adminEntity.get().toString());

        //2. 사용자 정의로 만든 CustomUserDetails정보를 이용해서 Entity정보를 Details에 전달한다.
        //SecurityConfig에서 설정할 내용은 없으면 CustomLoginSuccessHandler에서 작업을 진행한다.
        if (adminEntity.isPresent()) { // 관리자에 존재하면
            //List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(adminEntity.get().getRoleType().name()));
            return new CustomUserDetails(
                    adminEntity.get().getAdminEmail(),
                    adminEntity.get().getPassword(),
//                    adminEntity.get().getHotel(),
                    adminEntity.get().getLevel()
                    //authorities
            );
        }
        //일반회원 및 관리자에 존재하지 않으면 오류발생(Console)
        throw new UsernameNotFoundException("알 수 없는 아이디 : "+ AdminEmail);
    }
}
