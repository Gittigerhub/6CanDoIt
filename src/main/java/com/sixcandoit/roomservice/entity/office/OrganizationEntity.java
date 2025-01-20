package com.sixcandoit.roomservice.entity.office;

import com.sixcandoit.roomservice.entity.AdvertisementEntity;
import com.sixcandoit.roomservice.entity.event.EventEntity;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.AdminEntity;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "active_yn")
    private String activeYn;              // 활성화 유무

    // 관리자 회원 테이블과 1:N 매핑
    @OneToMany(mappedBy = "organizationEntity")
    private List<AdminEntity> adminEntities = new ArrayList<AdminEntity>();

    // 매장 상세 테이블과 1:1 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_detail_idx")
    private ShopDetailEntity shopDetailEntity;

    // 룸 테이블과 1:N 매핑
    @OneToMany(mappedBy = "organizationEntity")
    private List<RoomEntity> roomEntities = new ArrayList<RoomEntity>();

    // 광고 테이블과 1:N 매핑
    @OneToMany(mappedBy = "organizationEntity")
    private List<AdvertisementEntity> advertisementEntities = new ArrayList<AdvertisementEntity>();

    // 이벤트 테이블과 1:N 매핑
    @OneToMany(mappedBy = "organizationEntity")
    private List<EventEntity> eventEntities = new ArrayList<EventEntity>();

}