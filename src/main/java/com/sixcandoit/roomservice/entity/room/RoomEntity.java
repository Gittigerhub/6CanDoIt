package com.sixcandoit.roomservice.entity.room;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(name = "room_type")
    private String roomType;                // 객실 타입(STD:스탠다드, ROH:런 오브 하우스, MOD:모더레이트, SUP:슈페리어, DLX:디럭스, STU:스튜디오, JRSTE:쥬니어 스위트, STE:스위트, PH:팬트하우스)

    @Column(name = "room_price")
    private int roomPrice;                  // 객실 가격

    @Column(name = "room_season")
    private String roomSeason;              // 시즌 (peak:성수기, off:비 성수기)

    @Column(name = "room_condition")
    private String roomCondition;           // 객실 청소 여부( N:청소 미 완료, Y:청소 완료)

    @Column(name = "room_reservation")
    private int roomReservation;            // 객실 예약 상태(1:빈 방, 2:예약, 3:체크인, 4:체크 아웃)

    @Column(name = "room_smoking_yn")
    private String roomSmokingYn;           // 객실 흡연 가능 여부(N:불가능, Y:가능)

    @Column(name = "room_check_in")
    private LocalDateTime roomCheckIn;      // 체크인 가능 시간

    @Column(name = "room_check_out")
    private LocalDateTime roomCheckOut;     // 체크 아웃 시간

    @Column(name = "room_cancel_pee")
    private float roomCancelPee;            // 취소 수수료

    // 조직 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_idx")
    @JsonBackReference
    private OrganizationEntity organizationJoin;

    // 룸 테이블과 1:N 매핑
    @OneToMany(mappedBy = "roomJoin")
    private List<RoomImgEntity> roomImgJoin;

}