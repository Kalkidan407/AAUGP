package com.example.aaugp.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.aaugp.dto.projects.ProjectFilter;
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
    private final CurrentUserService currentUserService;
    private final AauStudentIdValidator aauStudentIdValidator;
    private final Map<ProjectListCacheKey, Page<ProjectResponse>> projectListCache = new ConcurrentHashMap<>();

    public ProjectResponse createProject(ProjectRequest request) {
        UserEntity currentUser = currentUserService.getCurrentUser();
        UserEntity projectOwner = resolveProjectOwner(request, currentUser);
        String projectStudentId = normalizeStudentId(projectOwner.getStudentId());
        AauStudentIdValidator.StudentAcademicInfo academicInfo =
                aauStudentIdValidator.validateFinalProjectEligibility(projectStudentId);

        if (projectRepository.existsByStudentId(projectStudentId)
                || projectRepository.existsByUserId(projectOwner.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Each AAU student can submit only one final project. Student id "
                            + projectStudentId + " already has a project.");
        }

        ProjectEntity project = fromDTO(request, projectOwner, projectStudentId, academicInfo);
        project.setCreatedAt(LocalDateTime.now());
        ProjectResponse response = toDTO(projectRepository.save(project));
        clearProjectListCache();
        return response;
    }

    public Page<ProjectResponse> getAllProjects(ProjectFilter filter, Pageable pageable) {
        ProjectListCacheKey cacheKey = ProjectListCacheKey.from(filter, pageable);
        return projectListCache.computeIfAbsent(cacheKey, key -> projectRepository.findAll(toSpecification(filter), pageable)
                .map(this::toDTO)
        );
    }

    public ProjectResponse getProjectById(Long id) {
        return toDTO(getProjectEntityById(id));
    }

    public List<ProjectResponse> getProjectsByStudentId(String studentId) {
        return projectRepository.findByUserStudentId(normalizeStudentId(studentId)).stream()
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
        ensureCanModifyProject(project);
        if (request.getStudentId() != null
                && !normalizeStudentId(request.getStudentId()).equals(project.getStudentId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project student id cannot be changed");
        }

        project.setTitle(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setGraduationYear(request.getGraduationYear());
        project.setGithubLink(request.getGithubLink());
        project.setDemoLink(request.getDemoLink());
        project.setImageUrl(request.getImageUrl());
        project.setDepartment(resolveDepartment(request.getDepartment()));
        ProjectResponse response = toDTO(projectRepository.save(project));
        clearProjectListCache();
        return response;
    }

    public void deleteProject(Long id) {
        ProjectEntity project = getProjectEntityById(id);
        ensureCanModifyProject(project);
        projectRepository.delete(project);
        clearProjectListCache();
    }

    private ProjectEntity fromDTO(
            ProjectRequest request,
            UserEntity projectOwner,
            String projectStudentId,
            AauStudentIdValidator.StudentAcademicInfo academicInfo) {
        ProjectEntity project = new ProjectEntity();
        project.setTitle(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setGraduationYear(request.getGraduationYear());
        project.setStudentId(projectStudentId);
        project.setStudentStartYearEc(academicInfo.startYearEc());
        project.setExpectedGraduationYearEc(academicInfo.expectedGraduationYearEc());
        project.setGithubLink(request.getGithubLink());
        project.setDemoLink(request.getDemoLink());
        project.setImageUrl(request.getImageUrl());
        project.setUser(projectOwner);
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
        response.setStudentId(project.getStudentId());
        response.setStudentStartYearEc(project.getStudentStartYearEc());
        response.setExpectedGraduationYearEc(project.getExpectedGraduationYearEc());
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
        return userRepository.findByStudentId(normalizeStudentId(studentId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with student id: " + studentId));
    }

    private UserEntity resolveProjectOwner(ProjectRequest request, UserEntity currentUser) {
        if (currentUserService.isAdmin(currentUser) && request.getStudentId() != null && !request.getStudentId().isBlank()) {
            return resolveUser(request.getStudentId());
        }

        String requestedStudentId = request.getStudentId();
        if (requestedStudentId != null
                && !requestedStudentId.isBlank()
                && !normalizeStudentId(requestedStudentId).equals(normalizeStudentId(currentUser.getStudentId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can submit a project only for your own student id");
        }

        return currentUser;
    }

    private void ensureCanModifyProject(ProjectEntity project) {
        UserEntity currentUser = currentUserService.getCurrentUser();
        if (currentUserService.isAdmin(currentUser)) {
            return;
        }
        if (project.getUser() == null || !project.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can update or delete only your own project");
        }
    }

    private String normalizeStudentId(String studentId) {
        return aauStudentIdValidator.normalizeStudentId(studentId);
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

    private Specification<ProjectEntity> toSpecification(ProjectFilter filter) {
        Specification<ProjectEntity> specification = Specification.unrestricted();
        if (filter == null) {
            return specification;
        }

        if (filter.department() != null && !filter.department().isBlank()) {
            String department = filter.department().trim().toLowerCase(Locale.ROOT);
            specification = specification.and((root, query, builder) ->
                    builder.equal(builder.lower(root.get("department").get("name")), department));
        }

        if (filter.studentId() != null && !filter.studentId().isBlank()) {
            String studentId = normalizeStudentId(filter.studentId());
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("studentId"), studentId));
        }

        if (filter.graduationYear() != null) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("graduationYear"), filter.graduationYear()));
        }

        if (filter.status() != null) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("status"), filter.status()));
        }

        if (filter.search() != null && !filter.search().isBlank()) {
            String search = "%" + filter.search().trim().toLowerCase(Locale.ROOT) + "%";
            specification = specification.and((root, query, builder) -> builder.or(
                    builder.like(builder.lower(root.get("title")), search),
                    builder.like(builder.lower(root.get("description")), search),
                    builder.like(builder.lower(root.get("githubLink")), search),
                    builder.like(builder.lower(root.get("demoLink")), search)
            ));
        }

        return specification;
    }

    private void clearProjectListCache() {
        projectListCache.clear();
    }

    private record ProjectListCacheKey(
            String search,
            String department,
            String studentId,
            Integer graduationYear,
            String status,
            int page,
            int size,
            String sort) {

        private static ProjectListCacheKey from(ProjectFilter filter, Pageable pageable) {
            return new ProjectListCacheKey(
                    normalize(filter == null ? null : filter.search()),
                    normalize(filter == null ? null : filter.department()),
                    normalizeStudentIdForKey(filter == null ? null : filter.studentId()),
                    filter == null ? null : filter.graduationYear(),
                    filter == null || filter.status() == null ? null : filter.status().name(),
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    pageable.getSort().toString());
        }

        private static String normalize(String value) {
            return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
        }

        private static String normalizeStudentIdForKey(String value) {
            return value == null || value.isBlank() ? null : value.trim().toUpperCase(Locale.ROOT);
        }
    }
}
