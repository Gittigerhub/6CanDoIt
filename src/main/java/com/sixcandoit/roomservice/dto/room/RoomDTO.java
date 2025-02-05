package com.sixcandoit.roomservice.dto.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private Integer idx;                    // 기본 키
    private String roomName;                // 객실 이름
    private String roomInfo;                // 객실 정보
    private String roomType;                // 객실 타입(STD:스탠다드, ROH:런 오브 하우스, MOD:모더레이트, SUP:슈페리어, DLX:디럭스, STU:스튜디오, JRSTE:쥬니어 스위트, STE:스위트, PH:팬트하우스)
    private int roomPrice;                  // 객실 가격
    private String roomSeason;              // 시즌 (peak:성수기, off:비 성수기)
    private String roomCondition;           // 객실 청소 여부( N:청소 미 완료, Y:청소 완료)
    private int roomReservation;            // 객실 예약 상태(1:빈 방, 2:예약, 3:체크인, 4:체크 아웃)
    private String roomSmokingYn;           // 객실 흡연 가능 여부(N:불가능, Y:가능)
    private LocalDateTime roomCheckIn;      // 체크인 가능 시간
    private LocalDateTime roomCheckOut;     // 체크 아웃 시간
    private float roomCancelPee;
}
