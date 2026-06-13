package com.example.aaugp.dto.projects;

import java.time.LocalDateTime;

import com.example.aaugp.model.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String projectName;
    private String description;
    private String department;
    private String studentId;
    private Integer studentStartYearEc;
    private Integer expectedGraduationYearEc;
    private String githubLink;
    private String demoLink;
    private String imageUrl;
    private Integer graduationYear;
    private Status status;
    private Integer starCount;
    private LocalDateTime createdAt;
}
