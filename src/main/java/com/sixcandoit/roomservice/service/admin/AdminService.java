package com.sixcandoit.roomservice.service.admin;

import com.sixcandoit.roomservice.config.CustomUserDetails;
import com.sixcandoit.roomservice.constant.Level;
import com.sixcandoit.roomservice.dto.admin.AdminDTO;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.repository.admin.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AdminService {

    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

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

    public void delete(String AdminEmail) {
        adminRepository.findByAdminEmail(AdminEmail);
    }

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
}
