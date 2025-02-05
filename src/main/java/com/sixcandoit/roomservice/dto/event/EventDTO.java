package com.sixcandoit.roomservice.dto.event;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class EventDTO {


    private Integer idx;                   // 기본 키


    private String eventTitle;             // 이벤트 제목


    private String eventTitleImg;          // 이벤트 상단 이미지 출력용


    private String eventContents;          // 이벤트 내용


    private LocalDateTime eventStartDate;  // 이벤트 시작일


    private LocalDateTime eventEndDate;    // 이벤트 종료일


    private String eventImg;               // 이벤트 이미지 경로 출력용


    private String activeYn;               // 활성유무 (N:비활성,Y:활성)


    private String eventState;             // 이벤트 상태(N:종료, Y:진행 중)

    // 관리자 회원 테이블과 N:1 매핑
    private OrganizationEntity organizationJoin;

    private MultipartFile titleFile;

    private MultipartFile contentFile;

}