package com.kjava.modules.m1.service;

import com.kjava.modules.m1.enums.ChecklistStatus;
import com.kjava.modules.m1.enums.EmployeeStatus;
import com.kjava.modules.m1.model.Checklist;
import com.kjava.modules.m1.model.Employee;
import com.kjava.modules.m1.repository.ChecklistRepository;
import com.kjava.modules.m1.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ChecklistRepository checklistRepository;

    @Transactional
    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByProfessionalEmail(employee.getProfessionalEmail())) {
            throw new RuntimeException("Email professionnel déjà existant.");
        }
        employee.setStatus(EmployeeStatus.BROUILLON);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee confirmEmployee(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé."));

        if (employee.getStatus() != EmployeeStatus.BROUILLON) {
            throw new RuntimeException("Seul un employé en statut BROUILLON peut être confirmé.");
        }

        // Vérification des champs obligatoires (RG-M1-02)
        if (employee.getFirstName() == null || employee.getLastName() == null ||
            employee.getBirthDate() == null || employee.getProfessionalEmail() == null ||
            employee.getDepartment() == null || employee.getPosition() == null) {
            throw new RuntimeException("Champs obligatoires manquants pour la confirmation.");
        }

        // Génération du matricule (RG-M1-01)
        String year = String.valueOf(LocalDate.now().getYear());
        long count = employeeRepository.countByRegistrationNumberStartingWith(year) + 1;
        String registrationNumber = String.format("EMP-%s-%04d", year, count);
        employee.setRegistrationNumber(registrationNumber);

        employee.setStatus(EmployeeStatus.CONFIRME);
        
        // Création automatique de la checklist (RG-M1-14)
        Checklist checklist = Checklist.builder()
                .employee(employee)
                .status(ChecklistStatus.BROUILLON)
                .completedItems(new ArrayList<>())
                .build();
        checklistRepository.save(checklist);

        // TODO: Déclencher création compte utilisateur et envoi email (RG-M1-05)

        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee registerDeparture(UUID employeeId, LocalDate departureDate, String reason) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé."));

        if (departureDate == null || reason == null) {
            throw new RuntimeException("Date de départ et motif obligatoires.");
        }

        employee.setStatus(EmployeeStatus.DEPART);
        employee.setDepartureDate(departureDate);
        employee.setDepartureReason(reason);

        // TODO: Révoquer les accès applicatifs (RG-M1-07)

        return employeeRepository.save(employee);
    }
}
