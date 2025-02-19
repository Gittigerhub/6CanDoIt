package com.sixcandoit.roomservice.dto.office;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalculateDTO {

    private Integer idx;

    private int useCount; //이용수

    private int salesPrice; //매출금액

    private int calculatePrice; //정산금액

    private String calculateState; //정산상태

    private DateTime saleDate; //매출일자

    private DateTime calculateDate;

    private float calculateFee;
}