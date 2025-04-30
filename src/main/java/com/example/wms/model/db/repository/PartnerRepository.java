package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findByName(String name);

    Optional<Partner> findByIdAndIsActiveTrue(Long id);

    Page<Partner> findAllByIsActiveTrue(Pageable pageRequest);

    boolean existsByIdAndIsActiveTrue(Long partnerId);
}
