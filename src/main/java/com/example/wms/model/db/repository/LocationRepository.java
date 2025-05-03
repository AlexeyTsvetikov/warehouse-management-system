package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Location;
import com.example.wms.model.enums.LocationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByName(String name);

    Optional<Location> findByNameAndIsActiveTrue(String name);

    Optional<Location> findByIdAndIsActiveTrue(Long id);

    Page<Location> findAllByIsActiveTrue(Pageable pageRequest);

    @Query("select l from Location l where l.locationType = :locationType")
    Page<Location> findAllFiltered(@Param("locationType") LocationType filter, Pageable pageRequest);
}
