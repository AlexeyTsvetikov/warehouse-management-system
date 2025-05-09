package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("select i from Inventory i where i.stock.id = :stockId")
    Page<Inventory> findAllFiltered(@Param("stockId")Long stockId, Pageable pageRequest);

}
