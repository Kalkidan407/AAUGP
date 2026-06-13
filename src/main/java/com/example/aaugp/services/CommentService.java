package com.example.aaugp.services;

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

import com.example.aaugp.dto.comment.CommentFilter;
import com.example.aaugp.dto.comment.CommentRequest;
import com.example.aaugp.dto.comment.CommentResponse;
import com.example.aaugp.model.CommentEntity;
import com.example.aaugp.model.ProjectEntity;
import com.example.aaugp.model.UserEntity;
import com.example.aaugp.repositories.CommentRepository;
import com.example.aaugp.repositories.ProjectRepository;
import com.example.aaugp.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final Map<CommentListCacheKey, Page<CommentResponse>> commentListCache = new ConcurrentHashMap<>();

    public CommentResponse createComment(CommentRequest request) {
        CommentEntity comment = new CommentEntity();
        comment.setContent(request.getContent());
        comment.setProject(resolveProject(request.getProjectId()));
        comment.setUser(resolveUser(request.getStudentId()));
        CommentResponse response = toDTO(commentRepository.save(comment));
        clearCommentListCache();
        return response;
    }

    public Page<CommentResponse> getAllComments(CommentFilter filter, Pageable pageable) {
        CommentListCacheKey cacheKey = CommentListCacheKey.from(filter, pageable);
        return commentListCache.computeIfAbsent(cacheKey, key -> commentRepository.findAll(toSpecification(filter), pageable)
                .map(this::toDTO));
    }

    public CommentResponse getCommentById(Long id) {
        return toDTO(getCommentEntityById(id));
    }

    public List<CommentResponse> getCommentsByProjectId(Long projectId) {
        return commentRepository.findByProjectId(projectId).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<CommentResponse> getCommentsByStudentId(String studentId) {
        return commentRepository.findByUserStudentId(studentId).stream()
                .map(this::toDTO)
                .toList();
    }

    public CommentResponse updateComment(Long id, CommentRequest request) {
        CommentEntity comment = getCommentEntityById(id);
        comment.setContent(request.getContent());
        comment.setProject(resolveProject(request.getProjectId()));
        comment.setUser(resolveUser(request.getStudentId()));
        CommentResponse response = toDTO(commentRepository.save(comment));
        clearCommentListCache();
        return response;
    }

    public void deleteComment(Long id) {
        commentRepository.delete(getCommentEntityById(id));
        clearCommentListCache();
    }

    private CommentResponse toDTO(CommentEntity comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        if (comment.getProject() != null) {
            response.setProjectId(comment.getProject().getId());
        }
        if (comment.getUser() != null) {
            response.setStudentId(comment.getUser().getStudentId());
        }
        return response;
    }

    private CommentEntity getCommentEntityById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found with id: " + id));
    }

    private ProjectEntity resolveProject(Long projectId) {
        if (projectId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project id is required");
        }
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found with id: " + projectId));
    }

    private UserEntity resolveUser(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student id is required");
        }
        return userRepository.findByStudentId(studentId.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with student id: " + studentId));
    }

    private Specification<CommentEntity> toSpecification(CommentFilter filter) {
        Specification<CommentEntity> specification = Specification.unrestricted();
        if (filter == null) {
            return specification;
        }

        if (filter.search() != null && !filter.search().isBlank()) {
            String search = "%" + filter.search().trim().toLowerCase(Locale.ROOT) + "%";
            specification = specification.and((root, query, builder) ->
                    builder.like(builder.lower(root.get("content")), search));
        }

        if (filter.projectId() != null) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("project").get("id"), filter.projectId()));
        }

        if (filter.studentId() != null && !filter.studentId().isBlank()) {
            String studentId = filter.studentId().trim().toUpperCase(Locale.ROOT);
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("user").get("studentId"), studentId));
        }

        return specification;
    }

    private void clearCommentListCache() {
        commentListCache.clear();
    }

    private record CommentListCacheKey(
            String search,
            Long projectId,
            String studentId,
            int page,
            int size,
            String sort) {

        private static CommentListCacheKey from(CommentFilter filter, Pageable pageable) {
            return new CommentListCacheKey(
                    normalize(filter == null ? null : filter.search()),
                    filter == null ? null : filter.projectId(),
                    normalizeStudentId(filter == null ? null : filter.studentId()),
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    pageable.getSort().toString());
        }

        private static String normalize(String value) {
            return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
        }

        private static String normalizeStudentId(String value) {
            return value == null || value.isBlank() ? null : value.trim().toUpperCase(Locale.ROOT);
        }
    }
}
