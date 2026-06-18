package com.example.aaugp.services;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
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
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setStudentId(aauStudentIdValidator.normalizeStudentId(request.getStudentId()));
        user.setEmail(normalizeEmail(request.getEmail()));
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
          );
        user.setRole(Role.USER);
        user.setDepartment(resolveDepartment(request.getDepartments()));
        return user;
    }

    private DepartmentEntity resolveDepartment(String departmentName) {
        if (departmentName == null || departmentName.isBlank()) {
            return null;
        }
        String normalizedDepartmentName = departmentName.trim();
        return departmentRepository.findByNameIgnoreCase(normalizedDepartmentName)
                .orElseGet(() -> {
                    try {
                        DepartmentEntity department = new DepartmentEntity();
                        department.setName(normalizedDepartmentName);
                        return departmentRepository.saveAndFlush(department);
                    } catch (DataIntegrityViolationException exception) {
                        return departmentRepository.findByNameIgnoreCase(normalizedDepartmentName)
                                .orElseThrow(() -> exception);
                    }
                });
    }

    public UserResponse createUser(UserRequest dto) {
        ensureEmailIsAvailable(normalizeEmail(dto.getEmail()), null);
        ensureStudentIdIsAvailable(aauStudentIdValidator.normalizeStudentId(dto.getStudentId()), null);
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
        String normalizedStudentId = aauStudentIdValidator.normalizeStudentId(studentId);
        UserEntity user = userRepository.findByStudentId(normalizedStudentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with student id: " + studentId));
        return toDTO(user);
    }

    public UserResponse updateUser(Long id, UserRequest dto) {
        UserEntity user = getUserEntityById(id);
        String normalizedEmail = normalizeEmail(dto.getEmail());
        String normalizedStudentId = aauStudentIdValidator.normalizeStudentId(dto.getStudentId());
        ensureEmailIsAvailable(normalizedEmail, user.getId());
        ensureStudentIdIsAvailable(normalizedStudentId, user.getId());

        user.setFirstName(dto.getFirstName().trim());
        user.setLastName(dto.getLastName().trim());
        user.setStudentId(normalizedStudentId);
        user.setEmail(normalizedEmail);
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

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void ensureEmailIsAvailable(String email, Long currentUserId) {
        userRepository.findByEmailIgnoreCase(email)
                .filter(existingUser -> currentUserId == null || !existingUser.getId().equals(currentUserId))
                .ifPresent(existingUser -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists: " + email);
                });
    }

    private void ensureStudentIdIsAvailable(String studentId, Long currentUserId) {
        userRepository.findByStudentId(studentId)
                .filter(existingUser -> currentUserId == null || !existingUser.getId().equals(currentUserId))
                .ifPresent(existingUser -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Student id already exists: " + studentId);
                });
    }

    
}
