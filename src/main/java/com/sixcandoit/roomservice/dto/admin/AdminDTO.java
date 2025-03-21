package com.sixcandoit.roomservice.dto.admin;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sixcandoit.roomservice.constant.Level;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.entity.notice.NoticeEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {

    private Integer idx;             // 기본 키
    private String adminEmail;            // 이메일
    private String password;              // 비밀번호
    private String adminName;             // 이름
    private String adminPhone;            // 연락처
//    private String activeYn;              // 활성 유무

    public AdminDTO setOrganDTO(OrganizationDTO organDTO){
         this.organDTO = organDTO;
         return this;
    }

    private Level level;
    //열거형 키(설명)
    private String levelDescription; //열거형 설명을 저장할 변수

    private OrganizationDTO organDTO;     // 조직 테이블 DTO

    // 조직명 끌고오기
    public String getOrganName() {
        return organDTO != null ? organDTO.getOrganName() : "호텔 없음";
    }

    //해당키와 설명을 저장하는 사용자 함수를 선언
    public void setLevel(Level level) {
        this.level = level; //키값 저장
        this.levelDescription =
                level != null?level.getDescription():null;
    }

    // 권한 설정
    public void setNewLevel(Level newLevel) {
        this.level = newLevel;
        this.levelDescription = newLevel != null ? newLevel.getDescription() : null;
    }

    //사용자 이름을 출력하는 메소드
    public String getDisplayAdminName() {
        return adminName;
    }

    // 조직 테이블과 N:1 매핑
    private OrganizationEntity organizationEntity;

    // 문의 사항 테이블과 1:N 매핑
    private List<QnaEntity> qnaEntities = new ArrayList<QnaEntity>();

    // 공지 사항 테이블과 1:N 매핑
    private List<NoticeEntity> noticeEntities = new ArrayList<NoticeEntity>();

}
