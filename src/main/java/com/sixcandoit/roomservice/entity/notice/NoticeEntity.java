package com.sixcandoit.roomservice.entity.notice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.admin.AdminEntity;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
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

    @Column(name = "notice_img")
    private String noticeImg;

    // 관리자 회원 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_idx")
    @JsonBackReference
    private AdminEntity adminJoin;

    // 이미지 파일 테이블과 1:N 매핑
    @OneToMany(mappedBy = "noticeJoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageFileEntity> imageFileJoin = new ArrayList<>(); // null값이면 코드작동 안하기 때문에 초기화 진행

    // 공지사항 생성과 동시에 이미지 추가
    public void addImage(ImageFileEntity image) {
        this.imageFileJoin.add(image);
        image.setNoticeJoin(this);
    }
    //이미지 URL을 noticeImg필드에 설정하는 메소드
    public void setNoticeImgFromImageFile(){
        if(imageFileJoin !=null && !imageFileJoin.isEmpty()){
            this.noticeImg = imageFileJoin.get(0).getUrl(); //첫번째 이미지를 대표 이미지로 설정
        }
    }
    //기존 이미지 업데이트
    public void updateImages(List<ImageFileEntity> existingImages){
        this.imageFileJoin.clear();
        for (ImageFileEntity existingImage : existingImages) {
            deleteImage(existingImage.getIdx());

            }
            setNoticeImgFromImageFile();
        }
        private void deleteImage(Integer idx){this.imageFileJoin.remove(idx);}

}
//public void deleteImage(ImageFileEntity image){
//    this.imageFileJoin.remove(image);
//    image.setNoticeJoin(null);
