package com.sixcandoit.roomservice.entity.admin;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.constant.Level;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.notice.NoticeEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "admin")
public class AdminEntity extends BaseEntity {

    @Id
    @Column(name = "admin_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                  // 기본 키

    @Column(name = "admin_email")
    private String adminEmail;            // 이메일

//    @Column(name = "admin_pwd")
    private String password;              // 비밀번호

    @Column(name = "admin_name")
    private String adminName;             // 이름

    @Column(name = "admin_phone")
    private String adminPhone;            // 관리자 연락처

//    @Column(name = "active_yn")
//    private String activeYn;              // 활성 유무

    // 관리자, 회원 구분을 위한 권한 분류
    @Enumerated(EnumType.STRING)
    private Level level;                  // 유저 권한

    // 기본값 설정
    @PrePersist
    public void prePersist() {
        System.out.println("prePersist 실행되었니?");
        if (this.level == null) {
            this.level = Level.GUEST; // 기본값 설정
            System.out.println("level 기본값 설정했어?" + this.level);
        }
    }

    // 조직 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_idx")
    @JsonBackReference
    private OrganizationEntity organizationJoin;

    // 문의 사항 테이블과 1:N 매핑
    @OneToMany(mappedBy = "adminJoin")
    private List<QnaEntity> qnaJoin;

    // 공지 사항 테이블과 1:N 매핑
    @OneToMany(mappedBy = "adminJoin")
    private List<NoticeEntity> noticeJoin;

}