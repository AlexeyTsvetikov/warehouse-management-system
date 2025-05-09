package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
