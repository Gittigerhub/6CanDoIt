package com.sixcandoit.roomservice.controller.office;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/office")
public class OfficeController {

    @GetMapping("/list")
    public String list() {

        return "office/officelist";

    }

    @GetMapping("/organ")
    public String organDetail() {

        return "office/organdetail";

    }

    @GetMapping("/shop")
    public String shopDetail() {

        return "office/shopdetail";

    }

}