package com.example.aaugp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aaugp.model.DepartmentEntity;

@Repository
public interface DepartmentRepository  extends JpaRepository<DepartmentEntity, Long>{
    Optional<DepartmentEntity> findByNameIgnoreCase(String name);
}
