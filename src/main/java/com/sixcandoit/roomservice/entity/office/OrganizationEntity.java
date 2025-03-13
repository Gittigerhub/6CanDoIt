package com.sixcandoit.roomservice.entity.office;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.AdvertisementEntity;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.event.EventEntity;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organization")
public class OrganizationEntity extends BaseEntity {

    @Id
    @Column(name = "organ_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;             // 기본 키

    @Column(name = "organ_name")
    private String organName;             // 조직 명

    @Column(name = "organ_type")
    private String organType;             // 조직 종류

    @Column(name = "organ_address")
    private String organAddress;          // 조직 주소

    @Column(name = "organ_tel")
    private String organTel;              // 조직 대표 연락처

    @Column(name = "active_yn")
    private String activeYn;              // 활성화 유무

    // 본사/지사 구분을 위한 자기참조 관계 (부모)
    @OneToMany(mappedBy = "head", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrganizationEntity> branches;    // 지사 목록

    // 본사/지사 구분을 위한 자기참조 관계 (자식)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id")
    @JsonBackReference
    private OrganizationEntity head;              // 본사

    // 본사/지사 구분을 위한 자기참조 관계 (부모)
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrganizationEntity> shops;      // 매장 목록

    // 본사/지사 구분을 위한 자기참조 관계 (자식)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    @JsonBackReference
    private OrganizationEntity branch;           // 지사

    // 관리자 회원 테이블과 1:N 매핑
    @OneToMany(mappedBy = "organizationJoin")
    private List<AdminEntity> adminJoin;

    // 매장 상세 테이블과 1:1 매핑
    @OneToOne(mappedBy = "organizationJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShopDetailEntity shopDetailJoin;

    // 메뉴 테이블과 1:N 매핑
    @OneToMany(mappedBy = "organizationJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuEntity> menuJoin;

    // 룸 테이블과 1:N 매핑
    @OneToMany(mappedBy = "organizationJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomEntity> roomJoin;

    // 광고 테이블과 1:N 매핑
    @OneToMany(mappedBy = "organizationJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdvertisementEntity> advertisementJoin;

    // 이벤트 테이블과 1:N 매핑
    @OneToMany(mappedBy = "organizationJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventEntity> eventJoin;

    // 이미지 파일 테이블과 1:N 매핑
    @OneToMany(mappedBy = "organizationJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageFileEntity> imageFileJoin = new ArrayList<>(); // null값이면 코드작동 안하기 때문에 초기화 진행

    // 조직 생성과 동시에 이미지 추가
    public void addImage(ImageFileEntity image) {
        this.imageFileJoin.add(image);
        image.setOrganizationJoin(this);
    }

    // 기존 이미지 업데이트
    public void updateImages(List<ImageFileEntity> newImages) {
        this.imageFileJoin.clear();
        for (ImageFileEntity image : newImages) {
            this.addImage(image);
        }
    }

}