package com.sixcandoit.roomservice.entity.qna;

import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.AdminEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
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

    @Column(name = "reply_hits")
    private int replyHits;               // 조회 수

    // 문의 사항 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_idx")
    private QnaEntity qnaEntity;

}