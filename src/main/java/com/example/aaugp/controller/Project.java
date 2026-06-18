package com.example.aaugp.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.aaugp.dto.projects.ProjectFilter;
import com.example.aaugp.dto.projects.ProjectRequest;
import com.example.aaugp.dto.projects.ProjectResponse;
import com.example.aaugp.model.Status;
import com.example.aaugp.services.ProjectService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
@Tag(name = "2. Projects")
public class Project {

    private final ProjectService projectService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@Valid @RequestBody ProjectRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping
    public Page<ProjectResponse> getAllProjects(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) Integer graduationYear,
            @RequestParam(required = false) Status status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        ProjectFilter filter = new ProjectFilter(search, department, studentId, graduationYear, status);
        return projectService.getAllProjects(filter, pageable);
    }

    @GetMapping("/id/{id}")
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @GetMapping("/student/{studentId}")
    public List<ProjectResponse> getProjectsByStudentId(@PathVariable String studentId) {
        return projectService.getProjectsByStudentId(studentId);
    }

    @GetMapping("/department/{departmentName}")
    public List<ProjectResponse> getProjectsByDepartment(@PathVariable String departmentName) {
        return projectService.getProjectsByDepartment(departmentName);
    }

    @PutMapping("/update/{id}")
    public ProjectResponse updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return projectService.updateProject(id, request);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
