package com.clinic.pharm.repository;

import com.clinic.pharm.domain.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrugRepository extends JpaRepository<Drug, Long> {
    Optional<Drug> findByBarcodeAndActiveTrue(String barcode);
}
