package com.sixcandoit.roomservice.controller.calculate;

import com.sixcandoit.roomservice.service.office.CalculateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
@Log

public class CalculateController {

    private final CalculateService calculateService;
    private final ModelMapper modelMapper;

    @GetMapping("/office/calculate/read")
    public String read(Model model){
        log.info("데이터를 읽어온다");

        return "office/calculateread";
    }
    @GetMapping("office/bo/calculate/read")
        public String readBo(Model model){
         log.info("BO");

         return "office/bo/calculateread";
    }

    @GetMapping("/office/ho/calculate/read")
        public String readHo(Model model){
        log.info("HO");

        return "office/ho/calculateread";
    }
}