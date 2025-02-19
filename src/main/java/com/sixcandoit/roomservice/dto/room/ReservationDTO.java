package com.sixcandoit.roomservice.dto.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {

    private Integer idx;                    // 기본 키
    private String resStatus;          // 예약 상태(1:취소, 2:예약, 3:체크인, 4:체크 아웃)
    private LocalDate startDate;       // 예약 시작 일자
    private LocalDate endDate;         // 예약 마지막 일자

    private Integer roomId;         // 룸 키
    private String roomType;        // 룸 타입
    private String roomName;        // 룸 이름
    private String roomPrice;       // 룸 가격
    private String memberName;      // 멤버 이름
    private String username;

    private LocalDateTime insDate;      // 작성 일자
    private LocalDateTime modDate;      // 수정 일자

}
