package com.kjava.modules.m1.service;

import com.kjava.modules.m1.model.Department;
import com.kjava.modules.m1.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(UUID id) {
        return departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Département non trouvé"));
    }

    private final EmployeeRepository employeeRepository;

    public void deleteDepartment(UUID id) {
        Department department = getDepartmentById(id);
        // RG-M1-11: La suppression d'un département est bloquée s'il contient des employés actifs.
        if (employeeRepository.existsByDepartmentAndStatus(department, EmployeeStatus.CONFIRME)) {
            throw new RuntimeException("Impossible de supprimer un département contenant des employés actifs.");
        }
        departmentRepository.delete(department);
    }
}
