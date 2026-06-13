package com.example.aaugp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.aaugp.model.DepartmentEntity;

@Repository
public interface DepartmentRepository  extends JpaRepository<DepartmentEntity, Long>, JpaSpecificationExecutor<DepartmentEntity> {
    Optional<DepartmentEntity> findByNameIgnoreCase(String name);
}
