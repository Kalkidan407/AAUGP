package com.example.aaugp.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.aaugp.dto.user.UserRequest;
import com.example.aaugp.dto.user.UserResponse;
import com.example.aaugp.model.DepartmentEntity;
import com.example.aaugp.model.Role;
import com.example.aaugp.model.UserEntity;
import com.example.aaugp.repositories.DepartmentRepository;
import com.example.aaugp.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServices {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AauStudentIdValidator aauStudentIdValidator;

    public UserResponse toDTO(UserEntity user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setStudentId(user.getStudentId());
        dto.setRole(user.getRole());
        if (user.getDepartment() != null) {
            dto.setDepartments(user.getDepartment().getName());
        }
        return dto;
    }

    private UserEntity fromDTO(UserRequest request) {
        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setStudentId(normalizeAndValidateStudentId(request.getStudentId()));
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setDepartment(resolveDepartment(request.getDepartments()));
        return user;
    }

    private DepartmentEntity resolveDepartment(String departmentName) {
        if (departmentName == null || departmentName.isBlank()) {
            return null;
        }
        return departmentRepository.findByNameIgnoreCase(departmentName.trim())
                .orElseGet(() -> {
                    DepartmentEntity department = new DepartmentEntity();
                    department.setName(departmentName.trim());
                    return departmentRepository.save(department);
                });
    }

    public UserResponse createUser(UserRequest dto) {
        UserEntity user = fromDTO(dto);
        UserEntity saved = userRepository.save(user);
        return toDTO(saved);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public UserResponse getUsersById(Long id) {
        UserEntity user = getUserEntityById(id);
        return toDTO(user);
    }

    public UserResponse getUserByStudentId(String studentId) {
        UserEntity user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with student id: " + studentId));
        return toDTO(user);
    }

    public UserResponse updateUser(Long id, UserRequest dto) {
        UserEntity user = getUserEntityById(id);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setStudentId(normalizeAndValidateStudentId(dto.getStudentId()));
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setDepartment(resolveDepartment(dto.getDepartments()));
        UserEntity updated = userRepository.save(user);
        return toDTO(updated);
    }

    public void deleteUserById(Long id) {
        UserEntity user = getUserEntityById(id);
        userRepository.delete(user);
    }

    private UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    private String normalizeAndValidateStudentId(String studentId) {
        String normalized = studentId == null ? null : studentId.trim().toUpperCase();
        aauStudentIdValidator.parse(normalized);
        return normalized;
    }
}
