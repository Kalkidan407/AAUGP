package com.example.aaugp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.aaugp.dto.comment.CommentRequest;
import com.example.aaugp.dto.comment.CommentResponse;
import com.example.aaugp.services.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class Comment {

    private final CommentService commentService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@Valid @RequestBody CommentRequest request) {
        return commentService.createComment(request);
    }

    @GetMapping
    public List<CommentResponse> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/id/{id}")
    public CommentResponse getCommentById(@PathVariable Long id) {
        return commentService.getCommentById(id);
    }

    @GetMapping("/project/{projectId}")
    public List<CommentResponse> getCommentsByProjectId(@PathVariable Long projectId) {
        return commentService.getCommentsByProjectId(projectId);
    }

    @GetMapping("/student/{studentId}")
    public List<CommentResponse> getCommentsByStudentId(@PathVariable String studentId) {
        return commentService.getCommentsByStudentId(studentId);
    }

    @PutMapping("/update/{id}")
    public CommentResponse updateComment(@PathVariable Long id, @Valid @RequestBody CommentRequest request) {
        return commentService.updateComment(id, request);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
