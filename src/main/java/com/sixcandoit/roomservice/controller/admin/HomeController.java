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

    // HO (Head Office) 페이지
    /* -----------------------------------------------------------------------------
        경로 : /ho (GET)
        인수 : HttpSession session, AdminDTO adminDTO
        출력 : "hoindex" 페이지
        설명 : HO 페이지로 이동하며, 세션에 관리자 이름을 저장하여 전달
    ----------------------------------------------------------------------------- */
    @GetMapping("/ho")
    public String hoIndexForm(HttpSession session, AdminDTO adminDTO) {
        log.info("HO 페이지 진입");
        session.setAttribute("adminName", adminDTO.getAdminName());
        return "hoindex";
    }

    // BO (Branch Office) 페이지
    /* -----------------------------------------------------------------------------
        경로 : /bo (GET)
        인수 : HttpSession session, AdminDTO adminDTO
        출력 : "boindex" 페이지
        설명 : BO 페이지로 이동하며, 세션에 관리자 이름을 저장하여 전달
    ----------------------------------------------------------------------------- */
    @GetMapping("/bo")
    public String boIndexForm(HttpSession session, AdminDTO adminDTO) {
        log.info("BO 페이지 진입");
        session.setAttribute("adminName", adminDTO.getAdminName());
        return "boindex";
    }

    // Guest 페이지
    /* -----------------------------------------------------------------------------
        경로 : /guest (GET)
        인수 : HttpSession session, AdminDTO adminDTO
        출력 : "guestindex" 페이지
        설명 : Guest 페이지로 이동하며, 세션에 관리자 이름을 저장하여 전달
    ----------------------------------------------------------------------------- */
    @GetMapping("/guest")
    public String guestIndexForm(HttpSession session, AdminDTO adminDTO) {
        log.info("게스트 페이지 진입");
        session.setAttribute("adminName", adminDTO.getAdminName());
        return "guestindex";
    }
}
