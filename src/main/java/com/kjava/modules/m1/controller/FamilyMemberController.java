package com.kjava.modules.m1.controller;

import com.kjava.modules.m1.model.FamilyMember;
import com.kjava.modules.m1.service.FamilyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/family-members")
@RequiredArgsConstructor
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;

    @PostMapping
    public ResponseEntity<FamilyMember> addFamilyMember(@RequestBody FamilyMember member) {
        return ResponseEntity.ok(familyMemberService.addFamilyMember(member));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<FamilyMember>> getFamilyByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(familyMemberService.getFamilyByEmployee(employeeId));
    }
}
