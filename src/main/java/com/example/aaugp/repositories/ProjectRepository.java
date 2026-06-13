package com.example.aaugp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aaugp.model.ProjectEntity;



public interface ProjectRepository  extends JpaRepository<ProjectEntity , Long>{
    List<ProjectEntity> findByUserStudentId(String studentId);
    List<ProjectEntity> findByDepartmentNameIgnoreCase(String departmentName);
}
