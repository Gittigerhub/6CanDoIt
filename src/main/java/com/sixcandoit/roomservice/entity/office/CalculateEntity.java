package com.sixcandoit.roomservice.entity.office;

import com.sixcandoit.roomservice.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "calculate")
public class CalculateEntity extends BaseEntity {

    @Id
    @Column(name = "calculate_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;                    // 기본 키

    @Column(name = "use_count")
    private int useCount;                   // 이용 수

    @Column(name = "sales_price")
    private int salesPrice;                 // 매출 금액

    @Column(name = "calculate_price")
    private int calculatePrice;             // 정산 금액

    @Column(name = "calculate_state")
    private String calculateState;          // 정산 상태(N:입금 전, Y:입금 완료)

    @Column(name = "sales_date")
    private LocalDateTime salesDate;        // 매출일자

    @Column(name = "calculate_date")
    private LocalDateTime calculateDate;    // 정산 일자

    @Column(name = "calculate_fee")
    private float calculate_fee;           // 정산 수수료

    // 매장 상세 테이블과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_detail_idx")
    private ShopDetailEntity shopDetailEntity;

}