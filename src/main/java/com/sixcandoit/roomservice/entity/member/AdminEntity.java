package com.sixcandoit.roomservice.entity.member;

import com.sixcandoit.roomservice.entity.NoticeEntity;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin")
public class AdminEntity extends BaseEntity {

    @Id
    @Column(name = "admin_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;             // 기본 키

    @Column(name = "admin_email")
    private String adminEmail;            // 이메일

    @Column(name = "admin_pwd")
    private String adminPwd;              // 비밀번호

    @Column(name = "admin_name")
    private String adminName;             // 생년월일

    @Column(name = "admin_phone")
    private String adminPhone;            // 성별

    @Column(name = "active_yn")
    private String activeYn;              // 이름

    // 조직 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_idx")
    private OrganizationEntity organizationEntity;

    // 문의 사항 테이블과 1:N 매핑
    @OneToMany(mappedBy = "adminEntity")
    private List<QnaEntity> qnaEntities = new ArrayList<QnaEntity>();

    // 공지 사항 테이블과 1:N 매핑
    @OneToMany(mappedBy = "adminEntity")
    private List<NoticeEntity> noticeEntities = new ArrayList<NoticeEntity>();

}