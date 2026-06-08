package com.example.aaugp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aaugp.model.ProjectEntity;



public interface ProjectRepository  extends JpaRepository<ProjectEntity , Long>{}
