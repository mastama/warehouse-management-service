package com.yolifay.warehousemanagementservice.service;

import com.yolifay.warehousemanagementservice.dto.VariantCreateRequest;
import com.yolifay.warehousemanagementservice.dto.VariantResponse;
import com.yolifay.warehousemanagementservice.dto.VariantUpdateRequest;
import com.yolifay.warehousemanagementservice.entity.Item;
import com.yolifay.warehousemanagementservice.entity.Variant;
import com.yolifay.warehousemanagementservice.repository.ItemRepository;
import com.yolifay.warehousemanagementservice.repository.VariantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VariantService {
    private final ItemRepository itemRepo;
    private final VariantRepository variantRepo;

    @Transactional
    public VariantResponse createVariant(UUID itemId, VariantCreateRequest req) {
        log.info("Start create variant");
        Item item = itemRepo.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Create varian by item not found with id " + itemId));

        Variant v = Variant.builder()
                .item(item)
                .sku(req.getSku().trim())
                .size(req.getSize())
                .color(req.getColor())
                .price(req.getPrice())
                .stock(req.getStock())
                .build();
        v = variantRepo.save(v);

        log.info("End create variant");
        return toResp(v);
    }

    @Transactional(readOnly = true)
    public List<VariantResponse> listVariantsByItem(UUID itemId) {
        itemRepo.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Get varian by item not found with id " + itemId));
        return variantRepo.findByItemId(itemId).stream().map(this::toResp).toList();
    }

    @Transactional(readOnly = true)
    public VariantResponse getVariant(UUID variantId) {
        return variantRepo.findById(variantId).map(this::toResp).orElseThrow(
                () -> new EntityNotFoundException("Get variant not found with id " + variantId));
    }

    @Transactional
    public VariantResponse updateVariant(UUID variantId, VariantUpdateRequest req) {
        log.info("Start update variant");
        Variant v = variantRepo.findById(variantId).orElseThrow(
                () -> new EntityNotFoundException("Update variant not found with id " + variantId));
        v.setSku(req.getSku().trim());
        v.setSize(req.getSize());
        v.setColor(req.getColor());
        v.setPrice(req.getPrice());
        v.setStock(req.getStock());

        log.info("End update variant");
        return toResp(variantRepo.save(v));
    }

    @Transactional
    public void deleteVariant(UUID variantId) {
        if (!variantRepo.existsById(variantId))
            throw new EntityNotFoundException("Delete variant not found");
        variantRepo.deleteById(variantId);
    }

    private VariantResponse toResp(Variant v) {
        return VariantResponse.builder()
                .id(v.getId())
                .itemId(v.getItem().getId())
                .sku(v.getSku())
                .size(v.getSize())
                .color(v.getColor())
                .price(v.getPrice())
                .stock(v.getStock())
                .version(v.getVersion())
                .build();
    }
}

