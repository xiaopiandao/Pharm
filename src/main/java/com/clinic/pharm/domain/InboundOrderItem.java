package com.clinic.pharm.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "inbound_order_item")
public class InboundOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private InboundOrder order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drug_id")
    private Drug drug;

    @Column(nullable = false, length = 64)
    private String scanCode;

    @Column(nullable = false, length = 64)
    private String batchNo;

    @Column(nullable = false)
    private LocalDate productionDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(nullable = false)
    private Integer quantity;
}
