package com.sixcandoit.roomservice.entity.qna;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
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
@Table(name = "reply")
public class ReplyEntity extends BaseEntity {

    @Id
    @Column(name = "reply_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                 // 기본 키

    @Column(name = "reply_title")
    private String replyTitle;           // 제목

    @Column(name = "reply_contents")
    private String replyContents;        // 내용

    // 문의 사항 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_idx")
    @JsonBackReference
    private QnaEntity qnaJoin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_idx")
    @JsonBackReference
    private AdminEntity admin;           // 답변 작성 관리자

    //@Column(name = "reply_hits")
    //private int replyHits;               // 조회 수

}