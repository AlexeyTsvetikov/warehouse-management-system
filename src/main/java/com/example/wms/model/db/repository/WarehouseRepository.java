package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Page<Warehouse> findAllByIsActiveTrue(Pageable pageable);

    Optional<Warehouse> findByIdAndIsActiveTrue(Long id);

    Optional<Warehouse> findWarehouseByName(String name);
}
