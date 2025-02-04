package com.sixcandoit.roomservice.entity.qna;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.AdminEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
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
@Table(name = "qna")
public class QnaEntity extends BaseEntity {

    @Id
    @Column(name = "qna_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                // 기본 키

    @Column(name = "qna_title")
    private String qnaTitle;           // 제목

    @Column(name = "qna_contents")
    private String qnaContents;        // 내용

    @Column(name = "qna_hits", columnDefinition = "integer default 0", nullable = false)
    private int qnaHits;               // 조회 수

    // 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    @JsonBackReference
    private MemberEntity memberJoin;

    // 관리자 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_idx")
    @JsonBackReference
    private AdminEntity adminJoin;

    // 문의 사항 댓글 테이블과 1:N 매핑
    @OneToMany(mappedBy = "qnaJoin")
    private List<ReplyEntity> replyJoin;

    private String qnaImg;              // 문의사항 이미지

}