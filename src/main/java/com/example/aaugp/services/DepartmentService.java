package com.example.aaugp.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.aaugp.dto.department.DepartmentRequest;
import com.example.aaugp.dto.department.DepartmentResponse;
import com.example.aaugp.model.DepartmentEntity;
import com.example.aaugp.repositories.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentResponse createDepartment(DepartmentRequest request) {
        DepartmentEntity department = new DepartmentEntity();
        department.setName(request.getDepartmentName());
        return toDTO(departmentRepository.save(department));
    }

    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public DepartmentResponse getDepartmentById(Long id) {
        return toDTO(getDepartmentEntityById(id));
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        DepartmentEntity department = getDepartmentEntityById(id);
        department.setName(request.getDepartmentName());
        return toDTO(departmentRepository.save(department));
    }

    public void deleteDepartment(Long id) {
        departmentRepository.delete(getDepartmentEntityById(id));
    }

    private DepartmentResponse toDTO(DepartmentEntity department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setDepartmentName(department.getName());
        return response;
    }

    private DepartmentEntity getDepartmentEntityById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Department not found with id: " + id));
    }
}
