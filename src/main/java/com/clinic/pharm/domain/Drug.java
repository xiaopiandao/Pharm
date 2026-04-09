package com.clinic.pharm.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "drug", indexes = {
        @Index(name = "idx_drug_name", columnList = "name"),
        @Index(name = "uk_drug_barcode", columnList = "barcode", unique = true)
})
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 128)
    private String genericName;

    @Column(length = 128)
    private String spec;

    @Column(nullable = false, length = 32)
    private String unit;

    @Column(length = 128)
    private String manufacturer;

    @Column(nullable = false, length = 64)
    private String barcode;

    @Column(length = 32)
    private String barcodeType;

    @Column(nullable = false)
    private Integer lowStockThreshold = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
