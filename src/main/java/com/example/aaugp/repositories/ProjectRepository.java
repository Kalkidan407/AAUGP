package com.example.aaugp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.aaugp.model.ProjectEntity;



public interface ProjectRepository  extends JpaRepository<ProjectEntity , Long>, JpaSpecificationExecutor<ProjectEntity>{
    List<ProjectEntity> findByUserStudentId(String studentId);
    boolean existsByStudentId(String studentId);
    boolean existsByUserId(Long userId);
    List<ProjectEntity> findByDepartmentNameIgnoreCase(String departmentName);
}
