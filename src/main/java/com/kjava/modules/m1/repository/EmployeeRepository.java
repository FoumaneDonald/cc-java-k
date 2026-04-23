package com.kjava.modules.m1.repository;

import com.kjava.modules.m1.enums.EmployeeStatus;
import com.kjava.modules.m1.model.Department;
import com.kjava.modules.m1.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByProfessionalEmail(String email);
    boolean existsByProfessionalEmail(String email);
    long countByRegistrationNumberStartingWith(String year);
    boolean existsByDepartmentAndStatus(Department department, EmployeeStatus status);
}
