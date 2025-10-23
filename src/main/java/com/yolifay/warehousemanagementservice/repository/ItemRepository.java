package com.yolifay.warehousemanagementservice.repository;

import com.yolifay.warehousemanagementservice.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByNameIgnoreCase(String name);
}

