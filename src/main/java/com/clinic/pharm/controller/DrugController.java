package com.clinic.pharm.controller;

import com.clinic.pharm.dto.DrugDtos;
import com.clinic.pharm.service.DrugService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
public class DrugController {

    private final DrugService drugService;

    @PostMapping
    public DrugDtos.DrugResponse create(@RequestBody @Valid DrugDtos.DrugCreateRequest request) {
        return drugService.createDrug(request);
    }

    @GetMapping
    public List<DrugDtos.DrugResponse> list() {
        return drugService.listDrugs();
    }

    @GetMapping("/by-barcode/{barcode}")
    public DrugDtos.DrugResponse findByBarcode(@PathVariable String barcode) {
        return drugService.getDrugResponseByBarcode(barcode);
    }
}
