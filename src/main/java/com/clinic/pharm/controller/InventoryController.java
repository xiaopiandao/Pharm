package com.clinic.pharm.controller;

import com.clinic.pharm.dto.DrugDtos;
import com.clinic.pharm.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/inbounds")
    public Map<String, String> inbound(@RequestBody @Valid DrugDtos.InboundCreateRequest request) {
        return Map.of("orderNo", inventoryService.createInbound(request));
    }

    @PostMapping("/outbounds")
    public Map<String, String> outbound(@RequestBody @Valid DrugDtos.OutboundCreateRequest request) {
        return Map.of("orderNo", inventoryService.createOutbound(request));
    }
}
