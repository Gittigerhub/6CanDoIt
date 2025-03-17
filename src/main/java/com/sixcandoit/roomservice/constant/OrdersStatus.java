package com.sixcandoit.roomservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrdersStatus {

    NEW("신규") , CHECK("접수") , COOKING("조리 중") ,
    CANCEL("취소") , CLOSE("완료");

    private final String description;

}
// ADMIN, DISTRIBUTOR, MEMBER 데이터베이스에 저장되는 값
// "관리자", "총판 관리자", "사용자"는 화면(HTML)에 출력할 내용