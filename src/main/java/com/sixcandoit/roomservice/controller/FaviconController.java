package com.sixcandoit.roomservice.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {

    @GetMapping("favicon.ico")
    public ResponseEntity<Resource> favicon(){
        Resource res = new ClassPathResource("/static/favicon.ico");
        return ResponseEntity.ok().body(res);

    }
}
