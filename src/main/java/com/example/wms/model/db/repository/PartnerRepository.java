package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Partner;
import com.example.wms.model.enums.PartnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findByName(String name);

    Optional<Partner> findByIdAndIsActiveTrue(Long id);

    Page<Partner> findAllByIsActiveTrue(Pageable pageRequest);

    @Query("select p from Partner p where p.partnerType = :partnerType")
    Page<Partner> findAllFiltered(@Param("partnerType") PartnerType filter, Pageable pageRequest);
}
