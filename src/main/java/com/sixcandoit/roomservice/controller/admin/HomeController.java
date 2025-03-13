package com.sixcandoit.roomservice.controller.admin;

import com.sixcandoit.roomservice.dto.admin.AdminDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
public class HomeController {

//    @GetMapping("/")
//    public String IndexForm(HttpSession session, AdminDTO adminDTO) {
//        log.info("최고 관리자 페이지 진입");
//        session.setAttribute("adminName", adminDTO.getAdminName());
//        return "index";
//    }

    @GetMapping("/ho")
    public String hoIndexForm(HttpSession session, AdminDTO adminDTO) {
        log.info("HO 페이지 진입");
        session.setAttribute("adminName", adminDTO.getAdminName());
        return "hoindex";
    }

    @GetMapping("/bo")
    public String boIndexForm(HttpSession session, AdminDTO adminDTO) {
        log.info("BO 페이지 진입");
        session.setAttribute("adminName", adminDTO.getAdminName());
        return "boindex";
    }

    @GetMapping("/guest")
    public String guestIndexForm(HttpSession session, AdminDTO adminDTO) {
        log.info("게스트 페이지 진입");
        session.setAttribute("adminName", adminDTO.getAdminName());
        return "guestindex";
    }
}
