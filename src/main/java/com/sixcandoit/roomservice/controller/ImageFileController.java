package com.sixcandoit.roomservice.controller;

import com.sixcandoit.roomservice.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/images")
public class ImageFileController {

    private final ImageFileService imageFileService;

    // 조직 삭제
    @GetMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> MemberPointDelete(@RequestParam Integer idx){

        try {
            // 들어온 idx값으로 이미지 삭제 진행
            imageFileService.deleteImage(idx);

            return  ResponseEntity.ok("이미지를 삭제하였습니다.");
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제를 실패 하였습니다.");
        }
    }

}