package com.grh.backend_m1.services;

import com.grh.backend_m1.entities.Employee;
import com.grh.backend_m1.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee saveEmployee(Employee employee) {
        // Validation RG-M1-03 (Email unique)
        if (repository.existsByEmailPro(employee.getEmailPro())) {
            throw new RuntimeException("Conflit : L'email professionnel existe déjà.");
        }
        
        // Logique pour RG-M1-01 (Génération matricule si absent)
        if (employee.getRegistrationNumber1() == null) {
            employee.setRegistrationNumber("RG-M1-" + System.currentTimeMillis()); 
        }

        return repository.save(employee);
    }
}