package com.example.aaugp.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public CommentResponse createComment(CommentRequest request) {
        CommentEntity comment = new CommentEntity();
        comment.setContent(request.getContent());
        comment.setProject(resolveProject(request.getProjectId()));
        comment.setUser(resolveUser(request.getStudentId()));
        return toDTO(commentRepository.save(comment));
    }

    public List<CommentResponse> getAllComments() {
        return commentRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
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
        return toDTO(commentRepository.save(comment));
    }

    public void deleteComment(Long id) {
        commentRepository.delete(getCommentEntityById(id));
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
        return userRepository.findByStudentId(studentId.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with student id: " + studentId));
    }
}
