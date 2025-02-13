package com.sixcandoit.roomservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.event.EventEntity;
import com.sixcandoit.roomservice.entity.notice.NoticeEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageFileEntity extends BaseEntity {

    @Id
    @Column(name = "image_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;            // 기본키

    @Column(name = "image_name")
    private String name;            // UUID + 이미지 원본 이름

    @Column(name = "origin_name")
    private String originName;      // 이미지 원본 이름

    @Column(name = "image_url")
    private String url;             // 이미지 경로

    @Column(name = "image_repimage_yn")
    private String repimageYn;      // 대표 이미지 여부

    public ImageFileEntity(String name, String originName, String url, String repimageYn) {
        this.name = name;
        this.originName = originName;
        this.url = url;
        this.repimageYn = repimageYn;
    }

    // 룸 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_idx")
    @JsonBackReference
    private RoomEntity roomJoin;

    // 광고 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_idx")
    @JsonBackReference
    private AdvertisementEntity advertisementJoin;

    // 조직 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_idx")
    @JsonBackReference
    private OrganizationEntity organizationJoin;

    // 이벤트 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_idx")
    @JsonBackReference
    private EventEntity eventJoin;

    // 메뉴 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_idx")
    @JsonBackReference
    private MenuEntity menuJoin;

    // qna 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_idx")
    @JsonBackReference
    private QnaEntity qnaJoin;

    // 공지사항 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_idx")
    @JsonBackReference
    private NoticeEntity noticeJoin;

}