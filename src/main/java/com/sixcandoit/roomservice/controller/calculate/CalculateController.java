package com.sixcandoit.roomservice.controller.calculate;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
@Log

public class CalculateController {

    @GetMapping("/office/calculateread")
    public String read(Model model){
        log.info("데이터를 읽어온다");

        return "/office/calculateread";
    }

}
