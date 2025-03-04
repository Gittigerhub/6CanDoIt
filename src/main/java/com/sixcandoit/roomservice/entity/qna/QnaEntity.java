package com.sixcandoit.roomservice.entity.qna;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
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

    @Column(name = "fav_yn")
    private String favYn;              // 자주 묻는 질문 설정 (Y: 활성, N: 비활성)

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

    // 이미지 파일 테이블과 1:N 매핑
    @OneToMany(mappedBy = "qnaJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageFileEntity> imageFileJoin  = new ArrayList<>(); // null값이면 코드작동 안하기 때문에 초기화 진행

    // qna 생성과 동시에 이미지 추가
    public void addImage(ImageFileEntity image) {
        this.imageFileJoin.add(image);
        image.setQnaJoin(this);
    }

    // 기존 이미지 업데이트
    public void updateImages(List<ImageFileEntity> images) {
        this.imageFileJoin.clear();
        for (ImageFileEntity image : images) {
            this.addImage(image);
        }
    }

}