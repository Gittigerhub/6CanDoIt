package com.sixcandoit.roomservice.dto.event;

import com.sixcandoit.roomservice.dto.member.MemberLoginDTO;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberPointDTO  {
    private Integer idx;                         //포인트 번호
    private int memberPoint;                     //현재 포인트
    private int memberPointTotal;                // 총 포인트
    private LocalDateTime memberPointStartDate;  // 포인트 발급일
    private LocalDateTime memberPointEndDate;    // 포인트 만료일
    private String memberPointOperationYn;       // 포인트 사용여부(N:미사용, Y:사용)
    private String memberPointContents;          // 포인트 내용
    private MemberLoginDTO memberJoin;                 // 회원 정보

}