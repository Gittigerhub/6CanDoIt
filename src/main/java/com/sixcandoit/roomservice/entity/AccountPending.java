package com.sixcandoit.roomservice.entity;

import com.sixcandoit.roomservice.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccountPending extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="accountPending_num")
    private Long num;

    private  String email; // 사용자가 입력한 이메일
    private  String authenticationCode; // 인증 코드

}
