package com.yolifay.warehousemanagementservice.repository;

import com.yolifay.warehousemanagementservice.entity.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, UUID> {
}
