package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.OperationDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OperationDetailRepository extends JpaRepository<OperationDetail, Long> {

    Page<OperationDetail> findByOperationId(Long operationId, Pageable pageRequest);


}
