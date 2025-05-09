package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Location;
import com.example.wms.model.db.entity.Product;
import com.example.wms.model.db.entity.Stock;
import com.example.wms.model.enums.StockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductAndLocation(Product product, Location location);

    Optional<Stock> findByIdAndStatus(Long id, StockStatus status);

    @Query("select s from Stock s where s.product.sku like %:filter% or s.location.name like %:filter%")
    Page<Stock> findAllFiltered(Pageable pageRequest, @Param("filter") String filter);
}
