package com.sixcandoit.roomservice.entity.office;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sixcandoit.roomservice.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "calculate")
public class CalculateEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "use_count")
    private DateTime useCount;

    @Column(name = "sales_price")
    private int salesPrice;

    @Column(name = "calculate_price")
    private int calculatePrice;

    @Column(name = "calculate_state")
    private int calculateState;  // N: 입금전, Y: 입금완료

    @Column(name = "sales_date")
    private DateTime salesDate;

    @Column(name = "calculate_date")
    private DateTime calculateDate;

    @Column(name = "calculate_fee")
    private int calculateFee;

    @ManyToOne
    @JoinColumn(name="shop_detail_id")
    @JsonBackReference
    private ShopDetailEntity shopDetail;
}
