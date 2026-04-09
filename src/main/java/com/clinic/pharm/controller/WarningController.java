package com.clinic.pharm.controller;

import com.clinic.pharm.dto.DrugDtos;
import com.clinic.pharm.service.WarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warnings")
@RequiredArgsConstructor
public class WarningController {

    private final WarningService warningService;

    @GetMapping("/low-stock")
    public List<DrugDtos.StockWarningResponse> lowStockWarnings() {
        return warningService.lowStockWarnings();
    }

    @GetMapping("/expiring")
    public List<DrugDtos.ExpiringWarningResponse> expiringWarnings(@RequestParam(required = false) Integer days) {
        return warningService.expiringWarnings(days);
    }
}
