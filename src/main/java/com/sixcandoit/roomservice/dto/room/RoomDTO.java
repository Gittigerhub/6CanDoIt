package com.sixcandoit.roomservice.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private Integer idx;                    // 기본 키

    @NotBlank
    @Size(min = 1, max = 30, message = "객실 이름을 입력하세요.")
    private String roomName;                // 객실 이름

    @NotBlank
    @Size(min = 1, message = "객실에 대한 상세 정보를 입력해주세요.")
    private String roomInfo;                // 객실 정보

    private String roomType;                // 객실 타입(STD:스탠다드, ROH:런 오브 하우스, MOD:모더레이트, SUP:슈페리어, DLX:디럭스, STU:스튜디오, JRSTE:쥬니어 스위트, STE:스위트, PH:팬트하우스)
    private String roomView;                // 객실 뷰 종류(O:오션뷰, C:시티뷰, M:마운틴뷰, G:가든뷰, N:창문 없음)
    private int roomNum = 2;                // 투숙객 수
    private int roomBed = 0;                // 침대 타입(3: 킹, 2: 퀸, 1: 더블, 0: 싱글)
    private int roomPrice = 0;                  // 객실 가격
    private int roomSize = 0;                 // 객실 평수
    private String roomSeason;              // 시즌 (peak:성수기, off:비 성수기)
    private String roomBreakfast = "N";              // 조식 여부 (Y: 있음, N: 없음)
    private String roomCondition;           // 객실 청소 여부( N:청소 미 완료, Y:청소 완료)
    private String roomSmokingYn = "N";           // 객실 흡연 가능 여부(N:불가능, Y:가능)
    private String roomWifi = "N";              // 와이파이 여부(N:없음, Y:있음)
    private String roomTv = "N";                // TV 여부(N:없음, Y:있음)
    private String roomAir = "N";               // 에어컨 여부(N:없음, Y:있음)
    private String roomBath = "N";              // 전용욕실 여부(N:없음, Y:있음)
    private LocalTime roomCheckIn;      // 체크인 가능 시간
    private LocalTime roomCheckOut;     // 체크 아웃 시간
    private float roomCancelPee;            // 취소 수수료
    private LocalDateTime insDate;      // 작성 일자
    private LocalDateTime modDate;      // 수정 일자
    private String resStatus;          // 예약 상태(1:빈 방, 2:예약, 3:체크인, 4:체크 아웃)

}
