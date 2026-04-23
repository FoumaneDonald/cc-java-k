package com.kjava.modules.m1.controller;

import com.kjava.modules.m1.model.Employee;
import com.kjava.modules.m1.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Employee> confirmEmployee(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.confirmEmployee(id));
    }

    @PostMapping("/{id}/departure")
    public ResponseEntity<Employee> registerDeparture(
            @PathVariable UUID id,
            @RequestParam LocalDate departureDate,
            @RequestParam String reason) {
        return ResponseEntity.ok(employeeService.registerDeparture(id, departureDate, reason));
    }
}
