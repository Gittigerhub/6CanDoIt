//package com.sixcandoit.roomservice.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Value("${uploadPath}")
//    private String uploadPath;
//
//
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry){
//        //ResourcehandlerRegistry : 자원의 정보를 가지고 있는 변수
//        //만약에 /upload/**(이후상관없음) 호출이 오면 file:///c:/data로 연결
//        registry.addResourceHandler("/upload/**").addResourceLocations(uploadPath);
//        //Resource에 외부 폴더를 연결하는것을 추가
//        registry.addResourceHandler("/images/**").addResourceLocations("file:///");
//    }
//
//
//}
