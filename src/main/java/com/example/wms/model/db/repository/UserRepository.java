package com.example.wms.model.db.repository;

import com.example.wms.model.db.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Page<User> findAllByIsActiveTrue(Pageable pageable);

    Optional<User> findByIdAndIsActiveTrue(Long id);

    @Query("select u from User u where u.role.name like %:filter%")
    Page<User> findAllFiltered(Pageable pageRequest, @Param("filter") String filter);
}
