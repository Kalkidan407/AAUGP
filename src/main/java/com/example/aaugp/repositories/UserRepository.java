package com.example.aaugp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aaugp.model.UserEntity;

@Repository
public interface UserRepository  extends JpaRepository<UserEntity, Long> {
    
}