package com.grh.backend_m1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grh.backend_m1.entities.Employee;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    // Spring fera le lien avec getEmailPro() de l'entité
    Optional<Employee> findByEmailPro(String emailPro);
    boolean existsByEmailPro(String emailPro);
}