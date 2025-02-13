package com.sixcandoit.roomservice.entity.event;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class EventEntity extends BaseEntity {

    @Id
    @Column(name = "event_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                   // 기본 키

    @Column(name = "event_title")
    private String eventTitle;             // 이벤트 제목

    @Column(name = "event_title_img")
    private String eventTitleImg;          // 이벤트 상단 이미지

    @Column(name = "event_contents")
    private String eventContents;          // 이벤트 내용

    @Column(name = "event_s_date")
    private LocalDateTime eventStartDate;  // 이벤트 시작일

    @Column(name = "event_e_date")
    private LocalDateTime eventEndDate;    // 이벤트 종료일

    @Column(name = "event_img")
    private String eventImg;               // 이벤트 이미지 경로

    @Column(name = "active_yn")
    private String activeYn;               // 활성유무 (N:비활성,Y:활성)

    @Column(name = "event_state")
    private String eventState;             // 이벤트 상태(N:종료, Y:진행 중)

    // 관리자 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_idx")
    @JsonBackReference
    private OrganizationEntity organizationJoin;

    // 이미지 파일 테이블과 1:N 매핑
    @OneToMany(mappedBy = "eventJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageFileEntity> imageFileJoin = new ArrayList<>(); // null값이면 코드작동 안하기 때문에 초기화 진행

    // 이벤트 생성과 동시에 이미지 추가
    public void addImage(ImageFileEntity image) {
        this.imageFileJoin.add(image);
        image.setEventJoin(this);
    }

    // 기존 이미지 업데이트
    public void updateImages(List<ImageFileEntity> newImages) {
        this.imageFileJoin.clear();
        for (ImageFileEntity image : newImages) {
            this.addImage(image);
        }
    }

}