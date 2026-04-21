package com.grh.backend_m1.controllers;

import com.grh.backend_m1.entities.Employee;
import com.grh.backend_m1.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Cette méthode manquait !
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    // Vérifiez le nom ici (save ou saveEmployee ?)
    public Employee save(Employee employee) {
        // Logique de matricule automatique
        if (employee.getRegistrationNumber1() == null || employee.getRegistrationNumber1().isEmpty()) {
            employee.setRegistrationNumber("RG-M1-" + System.currentTimeMillis());
        }
        return employeeRepository.save(employee);
    }
}