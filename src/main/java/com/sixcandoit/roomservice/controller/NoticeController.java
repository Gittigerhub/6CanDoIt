package com.sixcandoit.roomservice.controller;

import com.sixcandoit.roomservice.service.NoticeService;
import groovy.util.logging.Log;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/notice")
public class NoticeController {

}
//    private final NoticeService noticeService;

//
//@GetMapping("/register")
//
//    return "notice/register";
//
//}
//@PostMapping("/notice")
//return "redirect/users/a";
//        }
//        @GetMapping("/list")
//}
//@GetMapping("/read")
