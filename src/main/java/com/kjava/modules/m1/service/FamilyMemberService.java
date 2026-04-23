package com.kjava.modules.m1.service;

import com.kjava.modules.m1.model.FamilyMember;
import com.kjava.modules.m1.repository.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyMemberService {
    private final FamilyMemberRepository familyMemberRepository;

    public FamilyMember addFamilyMember(FamilyMember member) {
        return familyMemberRepository.save(member);
    }

    public List<FamilyMember> getFamilyByEmployee(UUID employeeId) {
        return familyMemberRepository.findByEmployeeId(employeeId);
    }
}
