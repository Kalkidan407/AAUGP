package com.example.aaugp.dto.projects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {
    private String projectName;
    private String description;
    private String department;
    private String studentId;
    private String githubLink;
    private String demoLink;
    private String imageUrl;
    private Integer graduationYear;
    

 
}