package com.example.aaugp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.aaugp.model.CommentEntity;

@Repository
public interface CommentRepository  extends JpaRepository<CommentEntity, Long>, JpaSpecificationExecutor<CommentEntity>{
    List<CommentEntity> findByProjectId(Long projectId);
    List<CommentEntity> findByUserStudentId(String studentId);
} 
