package com.example.aaugp.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Schema(example = "Abebe Kebede")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(example = "abebe@example.com")
    private String email;

    @Schema(example = "1", description = "Department ID - select from available departments")
    private Long departmentId;

    @NotBlank(message = "Student id is required")
    @JsonAlias({"studentid", "student_id"})
    @Schema(example = "UGR/1234/23")
    private String studentId;

    @NotBlank(message = "Password is required")
    @Schema(example = "StrongPassword123")
    private String password;
}
