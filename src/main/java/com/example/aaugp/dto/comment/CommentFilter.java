package com.example.aaugp.dto.comment;

public record CommentFilter(
        String search,
        Long projectId,
        String studentId) {
}
