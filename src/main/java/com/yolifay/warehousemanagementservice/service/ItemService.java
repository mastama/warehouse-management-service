package com.yolifay.warehousemanagementservice.service;

import com.yolifay.warehousemanagementservice.dto.ItemCreateRequest;
import com.yolifay.warehousemanagementservice.dto.ItemResponse;
import com.yolifay.warehousemanagementservice.dto.ItemUpdateRequest;
import com.yolifay.warehousemanagementservice.entity.Item;
import com.yolifay.warehousemanagementservice.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepo;

    @Transactional
    public ItemResponse createItem(ItemCreateRequest req) {
        log.info("Start create item");
        itemRepo.findByNameIgnoreCase(req.getName()).ifPresent(i -> {
            throw new IllegalArgumentException("Item name already exists: " + req.getName());
        });
        Item item = Item.builder()
                .name(req.getName().trim())
                .description(req.getDescription())
                .build();
        item = itemRepo.save(item);

        log.info("End create item");
        return toResp(item);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItem(UUID id) {
        return toResp(itemRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Get item not found with id: " + id)));
    }

    @Transactional
    public ItemResponse updateItem(UUID id, ItemUpdateRequest req) {
        log.info("Start update item");

        Item item = itemRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Update item not found with id: " + id));
        item.setName(req.getName().trim());
        item.setDescription(req.getDescription());

        log.info("End update item");
        return toResp(itemRepo.save(item));
    }

    @Transactional
    public void deleteItem(UUID id) {
        if (!itemRepo.existsById(id)) throw new EntityNotFoundException("Delete item not found with id: " + id);
        itemRepo.deleteById(id);
    }

    private ItemResponse toResp(Item i) {
        return ItemResponse.builder()
                .id(i.getId())
                .name(i.getName())
                .description(i.getDescription())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}

