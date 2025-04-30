package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String categoryName);

    Optional<Category> findByIdAndIsActiveTrue(Long id);

    Page<Category> findAllByIsActiveTrue(Pageable pageRequest);
}
