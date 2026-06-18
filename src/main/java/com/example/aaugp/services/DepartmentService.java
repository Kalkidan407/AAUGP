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

import com.example.aaugp.dto.department.DepartmentRequest;
import com.example.aaugp.dto.department.DepartmentResponse;
import com.example.aaugp.dto.department.DepartmentFilter;
import com.example.aaugp.model.DepartmentEntity;
import com.example.aaugp.repositories.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final Map<DepartmentListCacheKey, Page<DepartmentResponse>> departmentListCache = new ConcurrentHashMap<>();


    private DepartmentResponse toDTO(DepartmentEntity department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setDepartmentName(department.getName());
        return response;
    }



    public DepartmentResponse createDepartment(DepartmentRequest request) {
        DepartmentEntity department = new DepartmentEntity();
        department.setName(request.getDepartmentName());
        DepartmentResponse response = toDTO(departmentRepository.save(department));
        clearDepartmentListCache();
        return response;
    }

    public Page<DepartmentResponse> getAllDepartments(DepartmentFilter filter, Pageable pageable) {
        DepartmentListCacheKey cacheKey = DepartmentListCacheKey.from(filter, pageable);
        return departmentListCache.computeIfAbsent(cacheKey, key -> departmentRepository.findAll(
            toSpecification(filter), pageable)
                .map(this::toDTO));
    }

    public DepartmentResponse getDepartmentById(Long id) {
        return toDTO(getDepartmentEntityById(id));
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        DepartmentEntity department = getDepartmentEntityById(id);
        department.setName(request.getDepartmentName());
        DepartmentResponse response = toDTO(departmentRepository.save(department));
        clearDepartmentListCache();
        return response;
    }

    public void deleteDepartment(Long id) {
        departmentRepository.delete(getDepartmentEntityById(id));
        clearDepartmentListCache();
    }


    private DepartmentEntity getDepartmentEntityById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Department not found with id: " + id));
    }

    private Specification<DepartmentEntity> toSpecification(DepartmentFilter filter) {
        Specification<DepartmentEntity> specification = Specification.unrestricted();
        if (filter == null || filter.search() == null || filter.search().isBlank()) {
            return specification;
        }

        String search = "%" + filter.search().trim().toLowerCase(Locale.ROOT) + "%";
        return specification.and((root, query, builder) ->
                builder.like(builder.lower(root.get("name")), search));
    }

    private void clearDepartmentListCache() {
        departmentListCache.clear();
    }

    private record DepartmentListCacheKey(String search, int page, int size, String sort) {

        private static DepartmentListCacheKey from(DepartmentFilter filter, Pageable pageable) {
            return new DepartmentListCacheKey(
                    filter == null || filter.search() == null || filter.search().isBlank()
                            ? null
                            : filter.search().trim().toLowerCase(Locale.ROOT),
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    pageable.getSort().toString());
        }
    }
}
