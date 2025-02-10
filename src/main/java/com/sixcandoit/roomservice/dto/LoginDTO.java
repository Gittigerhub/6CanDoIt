package com.sixcandoit.roomservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginDTO {
    private String loginid;
    private Enum level;
}
