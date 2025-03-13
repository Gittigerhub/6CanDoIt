package com.sixcandoit.roomservice.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Integer idx;                // 객실 번호
    private Integer organ_idx;          // 조직 idx
    private String roomType;           // 객실 타입
    private String roomView;           // 객실 전망
    private String roomName;           // 객실 이름
    private Integer roomBed;           // 침대 유형
    private Integer roomSize;          // 객실 크기
    private Integer roomNum;           // 투숙객 수
    private Integer roomPrice;         // 객실 가격
    private String roomInfo;           // 객실 정보
    private String roomWifi;           // 무료 WiFi
    private String roomTv;             // TV
    private String roomAir;            // 에어컨
    private String roomBath;           // 전용 욕실
    private String roomBreakfast;      // 무료 조식 제공
    private String roomSmokingYn;      // 흡연 가능 여부
    private LocalTime roomCheckIn;     // 체크인 시간
    private LocalTime roomCheckOut;    // 체크아웃 시간
    private String roomSeason;         // 성수기 여부
    private String resStatus;          // 예약 상태
    private LocalDateTime regDate;     // 등록일
    private LocalDateTime modDate;     // 수정일
    private List<MultipartFile> imageFiles; // 객실 이미지 파일 목록
}
