package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Operation;
import com.example.wms.model.enums.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    @Query("select o from Operation o where o.operationType = :operationType")
    Page<Operation> findAllFiltered(@Param("operationType") OperationType operationType, Pageable pageable);
}
