package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Location;
import com.example.wms.model.db.entity.Product;
import com.example.wms.model.db.entity.Stock;
import com.example.wms.model.enums.StockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductAndLocation(Product product, Location location);

    Page<Stock> findAllByStatus(StockStatus stockStatus, Pageable pageRequest);

    Page<Stock> findByProduct(Product product, Pageable pageRequest);

    Page<Stock> findByLocation(Location location, Pageable pageRequest);
}
