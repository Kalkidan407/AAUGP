package com.example.aaugp.dto.projects;

import com.example.aaugp.model.Status;

public record ProjectFilter(
        String search,
        String department,
        String studentId,
        Integer graduationYear,
        Status status) {
}
