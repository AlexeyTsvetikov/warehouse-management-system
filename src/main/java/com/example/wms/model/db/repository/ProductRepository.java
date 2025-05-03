package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findBySkuAndIsActiveTrue(String sku);

    Optional<Product> findByIdAndIsActiveTrue(Long id);

    Page<Product> findAllByIsActiveTrue(Pageable pageRequest);

    @Query("select p from Product p where p.category.name like %:filter% or p.manufacturer.name like %:filter%")
    Page<Product> findAllFiltered(Pageable pageRequest, @Param("filter") String filter);
}
