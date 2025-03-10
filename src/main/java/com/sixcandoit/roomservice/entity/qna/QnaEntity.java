package com.sixcandoit.roomservice.entity.qna;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "qna")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QnaEntity extends BaseEntity {

    @Id
    @Column(name = "qna_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                // 게시글 번호

    @Column(name = "qna_title", nullable = false)
    private String qnaTitle;           // 제목

    @Column(name = "qna_contents", nullable = false)
    private String qnaContents;        // 내용

    @Column(name = "fav_yn", columnDefinition = "varchar(1) default 'N'")
    private String favYn = "N";              // 자주 묻는 질문 여부

    @Column(name = "reply_yn", columnDefinition = "varchar(1) default 'N'")
    private String replyYn = "N";            // 답변 여부

    @Column(name = "qna_hits", nullable = false)
    private Integer qnaHits = 0;             // 조회수

    @Column(name = "member_name")
    private String memberName;         // 작성자 이름

    @OneToMany(mappedBy = "qnaJoin", cascade = CascadeType.ALL)
    private List<ReplyEntity> replyList;  // 답변 목록

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

    // 이미지 파일 테이블과 1:N 매핑
    @OneToMany(mappedBy = "qnaJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageFileEntity> imageFileJoin = new ArrayList<>(); // null값이면 코드작동 안하기 때문에 초기화 진행

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

    @PrePersist
    public void prePersist() {
        if (this.favYn == null) {
            this.favYn = "N";
        }
        if (this.replyYn == null) {
            this.replyYn = "N";
        }
        if (this.qnaHits == null) {
            this.qnaHits = 0;
        }
    }

}