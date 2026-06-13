package com.example.aaugp.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.aaugp.dto.projects.ProjectRequest;
import com.example.aaugp.dto.projects.ProjectResponse;
import com.example.aaugp.model.DepartmentEntity;
import com.example.aaugp.model.ProjectEntity;
import com.example.aaugp.model.UserEntity;
import com.example.aaugp.repositories.DepartmentRepository;
import com.example.aaugp.repositories.ProjectRepository;
import com.example.aaugp.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public ProjectResponse createProject(ProjectRequest request) {
        ProjectEntity project = fromDTO(request);
        project.setCreatedAt(LocalDateTime.now());
        return toDTO(projectRepository.save(project));
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public ProjectResponse getProjectById(Long id) {
        return toDTO(getProjectEntityById(id));
    }

    public List<ProjectResponse> getProjectsByStudentId(String studentId) {
        return projectRepository.findByUserStudentId(studentId).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProjectResponse> getProjectsByDepartment(String departmentName) {
        return projectRepository.findByDepartmentNameIgnoreCase(departmentName).stream()
                .map(this::toDTO)
                .toList();
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        ProjectEntity project = getProjectEntityById(id);
        project.setTitle(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setGraduationYear(request.getGraduationYear());
        project.setGithubLink(request.getGithubLink());
        project.setDemoLink(request.getDemoLink());
        project.setImageUrl(request.getImageUrl());
        project.setUser(resolveUser(request.getStudentId()));
        project.setDepartment(resolveDepartment(request.getDepartment()));
        return toDTO(projectRepository.save(project));
    }

    public void deleteProject(Long id) {
        projectRepository.delete(getProjectEntityById(id));
    }

    private ProjectEntity fromDTO(ProjectRequest request) {
        ProjectEntity project = new ProjectEntity();
        project.setTitle(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setGraduationYear(request.getGraduationYear());
        project.setGithubLink(request.getGithubLink());
        project.setDemoLink(request.getDemoLink());
        project.setImageUrl(request.getImageUrl());
        project.setUser(resolveUser(request.getStudentId()));
        project.setDepartment(resolveDepartment(request.getDepartment()));
        return project;
    }

    private ProjectResponse toDTO(ProjectEntity project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setProjectName(project.getTitle());
        response.setDescription(project.getDescription());
        response.setGraduationYear(project.getGraduationYear());
        response.setGithubLink(project.getGithubLink());
        response.setDemoLink(project.getDemoLink());
        response.setImageUrl(project.getImageUrl());
        response.setStatus(project.getStatus());
        response.setStarCount(project.getStarCount());
        response.setCreatedAt(project.getCreatedAt());
        if (project.getUser() != null) {
            response.setStudentId(project.getUser().getStudentId());
        }
        if (project.getDepartment() != null) {
            response.setDepartment(project.getDepartment().getName());
        }
        return response;
    }

    private ProjectEntity getProjectEntityById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found with id: " + id));
    }

    private UserEntity resolveUser(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student id is required");
        }
        return userRepository.findByStudentId(studentId.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with student id: " + studentId));
    }

    private DepartmentEntity resolveDepartment(String departmentName) {
        if (departmentName == null || departmentName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department is required");
        }
        return departmentRepository.findByNameIgnoreCase(departmentName.trim())
                .orElseGet(() -> {
                    DepartmentEntity department = new DepartmentEntity();
                    department.setName(departmentName.trim());
                    return departmentRepository.save(department);
                });
    }
}
