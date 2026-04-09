package com.clinic.pharm.domain;

import com.clinic.pharm.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inventory_txn", indexes = {
        @Index(name = "idx_txn_batch_time", columnList = "batch_id, created_at")
})
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drug_id")
    private Drug drug;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id")
    private DrugBatch batch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TransactionType type;

    @Column(nullable = false)
    private Integer quantityChange;

    @Column(nullable = false)
    private Integer quantityAfter;

    @Column(nullable = false, length = 64)
    private String referenceNo;

    @Column(length = 128)
    private String remark;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
