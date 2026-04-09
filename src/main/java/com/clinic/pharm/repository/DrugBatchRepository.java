package com.clinic.pharm.repository;

import com.clinic.pharm.domain.Drug;
import com.clinic.pharm.domain.DrugBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DrugBatchRepository extends JpaRepository<DrugBatch, Long> {
    Optional<DrugBatch> findByDrugIdAndBatchNo(Long drugId, String batchNo);

    List<DrugBatch> findByDrugAndActiveTrueAndExpiryDateAfterOrderByExpiryDateAsc(Drug drug, LocalDate date);

    List<DrugBatch> findByAvailableQuantityGreaterThanAndExpiryDateBefore(Integer qty, LocalDate date);
}
