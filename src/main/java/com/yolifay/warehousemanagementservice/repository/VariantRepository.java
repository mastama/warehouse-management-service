package com.yolifay.warehousemanagementservice.repository;

import com.yolifay.warehousemanagementservice.entity.Variant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantRepository extends JpaRepository<Variant, UUID> {
    List<Variant> findByItemId(UUID itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Variant v where v.id = :id")
    Optional<Variant> findByIdForUpdate(@Param("id") UUID id);
}
