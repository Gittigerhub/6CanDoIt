package com.sixcandoit.roomservice.controller.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {
    @GetMapping("/")
    public String IndexForm() {
        return "index";
    }

    @GetMapping("/result")
    public String ResultForm() {    //모든 사용자 접근 가능
        return "result";
    }

    @GetMapping("/member")
    public String MemberForm() {  //Member 권한자만 접근 가능
        return "member";
    }

    @GetMapping("/admin")
    public String AdminForm() { //ADMIN 권한자만 접근 가능
        return "admin";
    }

    @GetMapping("/ho")
    public String HoForm() {
        return "ho";
    }

    @GetMapping("/bo")
    public String BoForm() {
        return "bo";
    }

    @GetMapping("/manager")
    public String ManagerForm() {
        return "manager";
    }

}
