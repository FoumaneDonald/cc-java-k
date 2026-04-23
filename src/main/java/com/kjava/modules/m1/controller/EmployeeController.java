package com.kjava.modules.m1.controller;

import com.kjava.common.response.ApiResponse;
import com.kjava.modules.m1.model.Employee;
import com.kjava.modules.m1.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Employee>> createEmployee(@RequestBody Employee employee) {
        Employee created = employeeService.createEmployee(employee);
        return ResponseEntity.ok(ApiResponse.<Employee>builder()
                .success(true)
                .data(created)
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.<List<Employee>>builder()
                .success(true)
                .data(employees)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getEmployeeById(@PathVariable UUID id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.<Employee>builder()
                .success(true)
                .data(employee)
                .build());
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<Employee>> confirmEmployee(@PathVariable UUID id) {
        Employee confirmed = employeeService.confirmEmployee(id);
        return ResponseEntity.ok(ApiResponse.<Employee>builder()
                .success(true)
                .data(confirmed)
                .build());
    }

    @PostMapping("/{id}/departure")
    public ResponseEntity<ApiResponse<Employee>> registerDeparture(
            @PathVariable UUID id,
            @RequestParam LocalDate departureDate,
            @RequestParam String reason) {
        Employee departed = employeeService.registerDeparture(id, departureDate, reason);
        return ResponseEntity.ok(ApiResponse.<Employee>builder()
                .success(true)
                .data(departed)
                .build());
    }
}
