package com.sixcandoit.roomservice.entity.room;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room")
public class RoomEntity extends BaseEntity {

    @Id
    @Column(name = "room_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                    // 기본 키

    @Column(name = "room_name")
    private String roomName;                // 객실 이름

    @Column(name = "room_info")
    private String roomInfo;                // 객실 정보

    @Column(name = "room_type")
    private String roomType;                // 객실 타입(STD:스탠다드, ROH:런 오브 하우스, MOD:모더레이트, SUP:슈페리어, DLX:디럭스, STU:스튜디오, JRSTE:쥬니어 스위트, STE:스위트, PH:팬트하우스)

    @Column(name = "room_view")
    private String roomView;                // 객실 뷰 종류(O:오션뷰, C:시티뷰, M:마운틴뷰, G:가든뷰, N:창문 없음)

    @Column(name = "room_num", nullable = false)
    private int roomNum = 2;                // 투숙객 수

    @Column(name = "room_bed", nullable = false)
    private int roomBed = 0;                // 침대 타입(3: 킹, 2: 퀸, 1: 더블, 0: 싱글)

    @Column(name = "room_price", nullable = false)
    private int roomPrice = 0;                  // 객실 가격

    @Column(name = "room_size", nullable = false)
    private int roomSize = 0;           // 객실 평수

    @Column(name = "room_season")
    private String roomSeason;              // 시즌 (peak:성수기, off:비 성수기)

    @Column(name = "room_breakfast", columnDefinition = "varchar(1) default 'N'")
    private String roomBreakfast = "N";              // 조식 여부 (Y: 있음, N: 없음)

    @Column(name = "room_condition")
    private String roomCondition;           // 객실 청소 여부( N:청소 미 완료, Y:청소 완료)

    @Column(name = "room_smoking_yn", columnDefinition = "varchar(1) default 'N'")
    private String roomSmokingYn = "N";           // 객실 흡연 가능 여부(N:불가능, Y:가능)

    @Column(name = "room_wifi", columnDefinition = "varchar(1) default 'N'")
    private String roomWifi = "N";           // 와이파이 여부(N:없음, Y:있음)

    @Column(name = "room_tv", columnDefinition = "varchar(1) default 'N'")
    private String roomTv = "N";           // TV 여부(N:없음, Y:있음)

    @Column(name = "room_air", columnDefinition = "varchar(1) default 'N'")
    private String roomAir = "N";           // 에어컨 여부(N:없음, Y:있음)

    @Column(name = "room_bath", columnDefinition = "varchar(1) default 'N'")
    private String roomBath = "N";           // 전용욕실 여부(N:없음, Y:있음)

    @Column(name = "room_check_in")
    private LocalTime roomCheckIn;  // 체크인 가능 시간

    @Column(name = "room_check_out")
    private LocalTime roomCheckOut; // 체크 아웃 시간

    @Column(name = "room_cancel_pee", columnDefinition = "float default 0.0")
    private float roomCancelPee;            // 취소 수수료

    @Column(name = "room_reservation", nullable = false)
    private int roomReservation = 0;            // 객실 예약 상태(0:빈 방, 1:예약, 2:체크인, 3:체크 아웃)

    // 조직 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_idx")
    @JsonBackReference
    private OrganizationEntity organizationJoin;

    // 룸 테이블과 1:N 매핑
    @OneToMany(mappedBy = "roomJoin")
    private List<RoomImgEntity> roomImgJoin;

}