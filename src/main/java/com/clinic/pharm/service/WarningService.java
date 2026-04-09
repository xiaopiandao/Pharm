package com.clinic.pharm.service;

import com.clinic.pharm.dto.DrugDtos;
import com.clinic.pharm.repository.DrugBatchRepository;
import com.clinic.pharm.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarningService {

    private final DrugRepository drugRepository;
    private final DrugBatchRepository batchRepository;

    @Value("${app.warning.expiring-days:90}")
    private int defaultExpiringDays;

    @Transactional(readOnly = true)
    public List<DrugDtos.StockWarningResponse> lowStockWarnings() {
        return drugRepository.findAll().stream().map(drug -> {
            int available = batchRepository.findByDrugAndActiveTrueAndExpiryDateAfterOrderByExpiryDateAsc(drug, LocalDate.now())
                    .stream().mapToInt(b -> b.getAvailableQuantity() == null ? 0 : b.getAvailableQuantity()).sum();
            return new DrugDtos.StockWarningResponse(drug.getId(), drug.getName(), drug.getBarcode(), available, drug.getLowStockThreshold());
        }).filter(w -> w.available() <= w.threshold()).toList();
    }

    @Transactional(readOnly = true)
    public List<DrugDtos.ExpiringWarningResponse> expiringWarnings(Integer days) {
        int d = days == null ? defaultExpiringDays : days;
        LocalDate target = LocalDate.now().plusDays(d);
        return batchRepository.findByAvailableQuantityGreaterThanAndExpiryDateBefore(0, target)
                .stream()
                .map(b -> new DrugDtos.ExpiringWarningResponse(
                        b.getId(),
                        b.getDrug().getName(),
                        b.getBatchNo(),
                        b.getExpiryDate(),
                        b.getAvailableQuantity()
                ))
                .toList();
    }
}
