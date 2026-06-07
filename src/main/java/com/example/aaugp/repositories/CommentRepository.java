package com.example.aaugp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aaugp.model.CommentEntity;

@Repository
public interface CommentRepository  extends JpaRepository<CommentEntity, Long>{

    
} 