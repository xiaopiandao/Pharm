package com.clinic.pharm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DrugDtos {

    public record DrugCreateRequest(
            @NotBlank String code,
            @NotBlank String name,
            String genericName,
            String spec,
            @NotBlank String unit,
            String manufacturer,
            @NotBlank String barcode,
            String barcodeType,
            @NotNull @Min(0) Integer lowStockThreshold
    ) {}

    public record DrugResponse(
            Long id,
            String code,
            String name,
            String spec,
            String unit,
            String barcode,
            Integer totalAvailable,
            Integer lowStockThreshold
    ) {}

    public record InboundCreateRequest(
            @NotBlank String supplierName,
            @NotNull List<InboundItemRequest> items
    ) {}

    public record InboundItemRequest(
            @NotBlank String scanCode,
            @NotBlank String batchNo,
            @NotNull LocalDate productionDate,
            @NotNull LocalDate expiryDate,
            @NotNull BigDecimal purchasePrice,
            @NotNull @Min(1) Integer quantity
    ) {}

    public record OutboundCreateRequest(
            @NotBlank String receiver,
            @NotNull List<OutboundItemRequest> items
    ) {}

    public record OutboundItemRequest(
            @NotBlank String scanCode,
            @NotNull @Min(1) Integer quantity
    ) {}

    public record StockWarningResponse(
            Long drugId,
            String drugName,
            String barcode,
            Integer available,
            Integer threshold
    ) {}

    public record ExpiringWarningResponse(
            Long batchId,
            String drugName,
            String batchNo,
            LocalDate expiryDate,
            Integer availableQuantity
    ) {}
}
