package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByNumber(String number);

    Optional<Document> findByIdAndIsActiveTrue(Long id);

    Page<Document> findAllByIsActiveTrue(Pageable pageRequest);
}
