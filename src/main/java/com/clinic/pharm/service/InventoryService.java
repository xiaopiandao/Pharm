package com.clinic.pharm.service;

import com.clinic.pharm.domain.Drug;
import com.clinic.pharm.domain.DrugBatch;
import com.clinic.pharm.domain.InboundOrder;
import com.clinic.pharm.domain.InboundOrderItem;
import com.clinic.pharm.domain.InventoryTransaction;
import com.clinic.pharm.domain.enums.TransactionType;
import com.clinic.pharm.dto.DrugDtos;
import com.clinic.pharm.exception.BusinessException;
import com.clinic.pharm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final DrugService drugService;
    private final DrugBatchRepository batchRepository;
    private final InboundOrderRepository inboundOrderRepository;
    private final OutboundOrderRepository outboundOrderRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    @Transactional
    public String createInbound(DrugDtos.InboundCreateRequest request) {
        InboundOrder order = new InboundOrder();
        order.setSupplierName(request.supplierName());
        order.setOrderNo(generateNo("IN"));

        for (DrugDtos.InboundItemRequest item : request.items()) {
            Drug drug = drugService.findByBarcode(item.scanCode());

            DrugBatch batch = batchRepository.findByDrugIdAndBatchNo(drug.getId(), item.batchNo())
                    .orElseGet(() -> {
                        DrugBatch newBatch = new DrugBatch();
                        newBatch.setDrug(drug);
                        newBatch.setBatchNo(item.batchNo());
                        newBatch.setProductionDate(item.productionDate());
                        newBatch.setExpiryDate(item.expiryDate());
                        newBatch.setPurchasePrice(item.purchasePrice());
                        newBatch.setQuantity(0);
                        newBatch.setAvailableQuantity(0);
                        return newBatch;
                    });

            batch.setQuantity(batch.getQuantity() + item.quantity());
            batch.setAvailableQuantity(batch.getAvailableQuantity() + item.quantity());
            batch = batchRepository.save(batch);

            InboundOrderItem orderItem = new InboundOrderItem();
            orderItem.setOrder(order);
            orderItem.setDrug(drug);
            orderItem.setScanCode(item.scanCode());
            orderItem.setBatchNo(item.batchNo());
            orderItem.setProductionDate(item.productionDate());
            orderItem.setExpiryDate(item.expiryDate());
            orderItem.setPurchasePrice(item.purchasePrice());
            orderItem.setQuantity(item.quantity());
            order.getItems().add(orderItem);

            InventoryTransaction txn = new InventoryTransaction();
            txn.setDrug(drug);
            txn.setBatch(batch);
            txn.setType(TransactionType.INBOUND);
            txn.setQuantityChange(item.quantity());
            txn.setQuantityAfter(batch.getAvailableQuantity());
            txn.setReferenceNo(order.getOrderNo());
            txn.setRemark("入库");
            inventoryTransactionRepository.save(txn);
        }
        inboundOrderRepository.save(order);
        return order.getOrderNo();
    }

    @Transactional
    public String createOutbound(DrugDtos.OutboundCreateRequest request) {
        com.clinic.pharm.domain.OutboundOrder order = new com.clinic.pharm.domain.OutboundOrder();
        order.setOrderNo(generateNo("OUT"));
        order.setReceiver(request.receiver());

        for (DrugDtos.OutboundItemRequest item : request.items()) {
            Drug drug = drugService.findByBarcode(item.scanCode());
            int need = item.quantity();

            List<DrugBatch> batches = batchRepository
                    .findByDrugAndActiveTrueAndExpiryDateAfterOrderByExpiryDateAsc(drug, LocalDate.now())
                    .stream()
                    .filter(b -> b.getAvailableQuantity() > 0)
                    .sorted(Comparator.comparing(DrugBatch::getExpiryDate))
                    .toList();

            int totalAvail = batches.stream().mapToInt(DrugBatch::getAvailableQuantity).sum();
            if (totalAvail < need) {
                throw new BusinessException("库存不足: " + drug.getName() + "，需要 " + need + "，可用 " + totalAvail);
            }

            for (DrugBatch batch : batches) {
                if (need <= 0) break;
                int deduct = Math.min(need, batch.getAvailableQuantity());
                batch.setAvailableQuantity(batch.getAvailableQuantity() - deduct);
                batchRepository.save(batch);
                need -= deduct;

                InventoryTransaction txn = new InventoryTransaction();
                txn.setDrug(drug);
                txn.setBatch(batch);
                txn.setType(TransactionType.OUTBOUND);
                txn.setQuantityChange(-deduct);
                txn.setQuantityAfter(batch.getAvailableQuantity());
                txn.setReferenceNo(order.getOrderNo());
                txn.setRemark("出库");
                inventoryTransactionRepository.save(txn);
            }

            com.clinic.pharm.domain.OutboundOrderItem orderItem = new com.clinic.pharm.domain.OutboundOrderItem();
            orderItem.setOrder(order);
            orderItem.setDrug(drug);
            orderItem.setScanCode(item.scanCode());
            orderItem.setQuantity(item.quantity());
            order.getItems().add(orderItem);
        }
        outboundOrderRepository.save(order);
        return order.getOrderNo();
    }

    private String generateNo(String prefix) {
        return prefix + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + System.currentTimeMillis();
    }
}
