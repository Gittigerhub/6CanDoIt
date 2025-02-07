package com.sixcandoit.roomservice.entity.notice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notice")
public class NoticeEntity extends BaseEntity {

    @Id
    @Column(name = "notice_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                  // 기본 키

    @Column(name = "notice_title")
    private String noticeTitle;           // 공지 사항 제목

    @Column(name = "notice_contents")
    private String noticeContents;        // 공지 사항 내용

    @Column(name = "notice_type")
    private String noticeType;            // 공지 타입(M:필독, N:공지)

    @Column(name = "notice_hits")
    private int noticeHits;               // 조회 수

    @Column(name = "notice_date")
    private LocalDate noticeDate;

    // 관리자 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_idx")
    @JsonBackReference
    private AdminEntity adminJoin;


    }
