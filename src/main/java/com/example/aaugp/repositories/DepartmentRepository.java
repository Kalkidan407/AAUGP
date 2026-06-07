package com.example.aaugp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aaugp.model.DepartmentEntity;

@Repository
public interface DepartmentRepository  extends JpaRepository<DepartmentEntity, Long>{

    
}