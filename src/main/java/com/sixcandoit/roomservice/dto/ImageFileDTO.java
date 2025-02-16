package com.sixcandoit.roomservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageFileDTO {

    private Integer idx;            // 기본키

    private String name;            // UUID + 이미지 원본 이름

    private String originName;      // 이미지 원본 이름

    private String url;             // 이미지 경로

    private String repimageYn;      // 대표 이미지 여부

}