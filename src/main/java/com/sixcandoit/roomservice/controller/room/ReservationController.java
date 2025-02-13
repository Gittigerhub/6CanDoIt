package com.sixcandoit.roomservice.controller.room;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReservationController {

    @GetMapping("/room/roomres")
    public String roomres() { //테스트
        return "room/roomres";
    }
}
