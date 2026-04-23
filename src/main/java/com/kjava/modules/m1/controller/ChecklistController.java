package com.kjava.modules.m1.controller;

import com.kjava.modules.m1.model.Checklist;
import com.kjava.modules.m1.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checklists")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistService;

    @PutMapping("/employee/{employeeId}/items")
    public ResponseEntity<Checklist> updateChecklistItems(
            @PathVariable UUID employeeId,
            @RequestBody List<String> items) {
        return ResponseEntity.ok(checklistService.updateChecklistItems(employeeId, items));
    }

    @PostMapping("/employee/{employeeId}/validate")
    public ResponseEntity<Checklist> validateChecklist(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(checklistService.validateChecklist(employeeId));
    }
}
