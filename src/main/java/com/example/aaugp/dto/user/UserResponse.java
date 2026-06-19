package com.example.aaugp.dto.user;

import com.example.aaugp.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String studentId;
    private String departments;
    private Long departmentId;
    private Role role;

    
}