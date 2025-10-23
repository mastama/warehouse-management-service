package com.yolifay.warehousemanagementservice.service;

import com.yolifay.warehousemanagementservice.dto.OrderCreateRequest;
import com.yolifay.warehousemanagementservice.dto.OrderLineRequest;
import com.yolifay.warehousemanagementservice.dto.OrderResponse;
import com.yolifay.warehousemanagementservice.entity.SalesOrder;
import com.yolifay.warehousemanagementservice.entity.SalesOrderLine;
import com.yolifay.warehousemanagementservice.entity.Variant;
import com.yolifay.warehousemanagementservice.repository.SalesOrderRepository;
import com.yolifay.warehousemanagementservice.repository.VariantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final VariantRepository variantRepo;
    private final SalesOrderRepository orderRepo;

    /**
     * Mencegah oversell:
     * - Lock baris Variant (PESSIMISTIC_WRITE) via findByIdForUpdate
     * - Cek stok >= qty untuk semua line sebelum mengurangi
     */
    @Transactional
    public OrderResponse create(OrderCreateRequest req) {
        log.info("Start create order");
        if (req.getLines() == null || req.getLines().isEmpty())
            throw new IllegalArgumentException("Lines must not be empty");

        // Ambil dan lock semua variant yang diperlukan
        Map<UUID, Integer> requested = new LinkedHashMap<>();
        for (OrderLineRequest l : req.getLines()) {
            requested.merge(l.getVariantId(), l.getQuantity(), Integer::sum);
        }

        Map<UUID, Variant> locked = new HashMap<>();
        for (UUID vid : requested.keySet()) {
            Variant v = variantRepo.findByIdForUpdate(vid).orElseThrow(
                    () -> new EntityNotFoundException("Variant not found: " + vid));
            locked.put(vid, v);
        }

        // Validasi stok cukup untuk SEMUA line
        for (Map.Entry<UUID, Integer> e : requested.entrySet()) {
            Variant v = locked.get(e.getKey());
            int need = e.getValue();
            if (v.getStock() < need) {
                throw new IllegalStateException(
                        "Out of stock for SKU=" + v.getSku() + " (need " + need + ", available " + v.getStock() + ")");
            }
        }

        // Kurangi stok & bangun SalesOrder
        SalesOrder order = SalesOrder.builder()
                .grandTotal(BigDecimal.ZERO)
                .build();

        List<OrderResponse.Line> respLines = new ArrayList<>();
        BigDecimal grand = BigDecimal.ZERO;

        for (OrderLineRequest l : req.getLines()) {
            Variant v = locked.get(l.getVariantId());

            // reduce
            v.setStock(v.getStock() - l.getQuantity());

            BigDecimal unit = v.getPrice();
            BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(l.getQuantity()));

            SalesOrderLine sol = SalesOrderLine.builder()
                    .order(order)
                    .variant(v)
                    .quantity(l.getQuantity())
                    .unitPrice(unit)
                    .lineTotal(lineTotal)
                    .build();
            order.getLines().add(sol);
            grand = grand.add(lineTotal);

            respLines.add(OrderResponse.Line.builder()
                    .variantId(v.getId()).sku(v.getSku())
                    .quantity(l.getQuantity())
                    .unitPrice(unit)
                    .lineTotal(lineTotal)
                    .build());
        }

        order.setGrandTotal(grand);
        order = orderRepo.save(order); // cascade simpan lines

        // Simpan perubahan stok (JPA dirty checking)
        // variantRepo.saveAll(locked.values()); // tidak wajib jika managed

        log.info("End create order");
        return OrderResponse.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .grandTotal(order.getGrandTotal())
                .lines(respLines)
                .build();
    }
}

