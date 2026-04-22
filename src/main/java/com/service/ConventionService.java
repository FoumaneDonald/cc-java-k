package com.service;

import com.dto.request.CreateConventionDTO;
import com.entities.Convention;
import com.exception.ResourceNotFoundException;
import com.repository.ConventionRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConventionService {

	@Autowired private ConventionRepository conventionRepository;

    @Transactional
    public Convention create(CreateConventionDTO dto) {
        Convention convention = new Convention();
        convention.setName(dto.getName());
        convention.setOverrideAnnualDays(dto.getOverrideAnnualDays());
        convention.setMinNoticeDays(dto.getMinNoticeDays());
        convention.setEligibilityConditions(dto.getEligibilityConditions());
        return conventionRepository.save(convention);
    }

    @Transactional
    public Convention update(UUID id, CreateConventionDTO dto) {
        Convention convention = findById(id);
        convention.setName(dto.getName());
        convention.setOverrideAnnualDays(dto.getOverrideAnnualDays());
        convention.setMinNoticeDays(dto.getMinNoticeDays());
        convention.setEligibilityConditions(dto.getEligibilityConditions());
        return conventionRepository.save(convention);
    }

    @Transactional(readOnly = true)
    public Convention findById(UUID id) {
        return conventionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Convention not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Convention> findAll() {
        return conventionRepository.findAll();
    }

    @Transactional
    public void delete(UUID id) {
        Convention convention = findById(id);
        conventionRepository.delete(convention);
    }
}
