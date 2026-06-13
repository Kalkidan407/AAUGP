package com.example.aaugp.dto.user;

import com.example.aaugp.model.Role;

public record UserFilter(
        String search,
        String department,
        String studentId,
        Role role) {
}
