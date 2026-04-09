package com.clinic.pharm.service;

import com.clinic.pharm.domain.Drug;
import com.clinic.pharm.dto.DrugDtos;
import com.clinic.pharm.exception.NotFoundException;
import com.clinic.pharm.repository.DrugBatchRepository;
import com.clinic.pharm.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DrugService {

    private final DrugRepository drugRepository;
    private final DrugBatchRepository batchRepository;

    @Transactional
    public DrugDtos.DrugResponse createDrug(DrugDtos.DrugCreateRequest request) {
        Drug d = new Drug();
        d.setCode(request.code());
        d.setName(request.name());
        d.setGenericName(request.genericName());
        d.setSpec(request.spec());
        d.setUnit(request.unit());
        d.setManufacturer(request.manufacturer());
        d.setBarcode(request.barcode());
        d.setBarcodeType(request.barcodeType());
        d.setLowStockThreshold(request.lowStockThreshold());

        Drug saved = drugRepository.save(d);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Drug findByBarcode(String barcode) {
        return drugRepository.findByBarcodeAndActiveTrue(barcode)
                .orElseThrow(() -> new NotFoundException("未找到条码对应药品: " + barcode));
    }

    @Transactional(readOnly = true)
    public DrugDtos.DrugResponse getDrugResponseByBarcode(String barcode) {
        return toResponse(findByBarcode(barcode));
    }

    @Transactional(readOnly = true)
    public List<DrugDtos.DrugResponse> listDrugs() {
        return drugRepository.findAll().stream().map(this::toResponse).toList();
    }

    private DrugDtos.DrugResponse toResponse(Drug drug) {
        int total = batchRepository.findByDrugAndActiveTrueAndExpiryDateAfterOrderByExpiryDateAsc(drug, LocalDate.now())
                .stream().mapToInt(b -> b.getAvailableQuantity() == null ? 0 : b.getAvailableQuantity()).sum();
        return new DrugDtos.DrugResponse(
                drug.getId(),
                drug.getCode(),
                drug.getName(),
                drug.getSpec(),
                drug.getUnit(),
                drug.getBarcode(),
                total,
                drug.getLowStockThreshold());
    }
}
