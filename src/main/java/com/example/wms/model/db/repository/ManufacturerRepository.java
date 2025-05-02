package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Manufacturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

    Optional<Manufacturer> findByName(String name);

    Optional<Manufacturer> findByNameAndIsActiveTrue(String name);

    Optional<Manufacturer> findByIdAndIsActiveTrue(Long id);

    Page<Manufacturer> findAllByIsActiveTrue(Pageable pageRequest);
}
