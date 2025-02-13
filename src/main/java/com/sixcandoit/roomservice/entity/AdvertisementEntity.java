package com.sixcandoit.roomservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "advertisement")
public class AdvertisementEntity extends BaseEntity {

    @Id
    @Column(name = "ad_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                 // 기본 키

    @Column(name = "ad_title")
    private String adTitle;              // 광고 제목

    @Column(name = "ad_link_url")
    private String adLinkUrl;            // 광고 링크

    @Column(name = "ad_s_date")
    private LocalDateTime adStartDate;   // 광고 시작일

    @Column(name = "ad_e_date")
    private LocalDateTime adEndDate;     // 광고 종료일

    @Column(name = "ad_img_url")
    private String adImgUrl;             // 광고 이미지 경로

    @Column(name = "ad_state")
    private String adState;              // 상태(N:안함, Y:진행중)

    @Column(name = "ad_hits")
    private int adHits;                  // 조회 수

    // 조직 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_idx")
    @JsonBackReference
    private OrganizationEntity organizationJoin;

    // 이미지 파일 테이블과 1:N 매핑
    @OneToMany(mappedBy = "advertisementJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageFileEntity> imageFileJoin  = new ArrayList<>(); // null값이면 코드작동 안하기 때문에 초기화 진행

    // 광고 생성과 동시에 이미지 추가
    public void addImage(ImageFileEntity image) {
        this.imageFileJoin.add(image);
        image.setAdvertisementJoin(this);
    }

    // 기존 이미지 업데이트
    public void updateImages(List<ImageFileEntity> newImages) {
        this.imageFileJoin.clear();
        for (ImageFileEntity image : newImages) {
            this.addImage(image);
        }
    }

}