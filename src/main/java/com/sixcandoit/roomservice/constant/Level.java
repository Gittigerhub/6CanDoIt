package com.sixcandoit.roomservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Level {

    ADMIN("최고 관리자"),
    HO("본사 관리자"), BO("지사 관리자"), GUEST("관리자 승인 대기"),
    MEMBER("일반 사용자");

    private final String description;

}
// ADMIN, DISTRIBUTOR, MEMBER 데이터베이스에 저장되는 값
// "관리자", "총판 관리자", "사용자"는 화면(HTML)에 출력할 내용