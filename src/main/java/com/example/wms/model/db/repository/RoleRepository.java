package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);

    Optional<Role> findByIdAndIsActiveTrue(Long id);

    Page<Role> findAllByIsActiveTrue(Pageable pageable);
}
