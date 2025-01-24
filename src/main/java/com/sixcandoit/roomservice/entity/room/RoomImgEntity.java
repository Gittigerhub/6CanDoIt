package com.sixcandoit.roomservice.entity.room;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room_img")
public class RoomImgEntity extends BaseEntity {

    @Id
    @Column(name = "room_img_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                   // 기본 키

    @Column(name = "room_img_name")
    private String roomImgName;            // 객실 이미지 이름

    @Column(name = "room_img_type")
    private String roomImgType;            // 대표 이미지 여부(R:대표 이미지, G:일반 이미지)

    @Column(name = "room_img_url")
    private String roomImgUrl;             // 객실 이미지 경로

    // 룸 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_idx")
    @JsonBackReference
    private RoomEntity roomJoin;

}