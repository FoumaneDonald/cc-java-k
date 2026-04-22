package com.service;

import com.dto.request.CreateLeaveTypeDTO;
import com.entities.Convention;
import com.entities.LeaveType;
import com.exception.BusinessException;
import com.exception.ResourceNotFoundException;
import com.repository.ConventionRepository;
import com.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {

	@Autowired private LeaveTypeRepository leaveTypeRepository;
	@Autowired private ConventionRepository conventionRepository;

    @Transactional
    public LeaveType create(CreateLeaveTypeDTO dto) {
        // RG-M2-11: code must be unique
        if (leaveTypeRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("DUPLICATE_CODE",
                    "A leave type with code '" + dto.getCode() + "' already exists");
        }

        LeaveType type = new LeaveType();
        type.setName(dto.getName());
        type.setCode(dto.getCode().toUpperCase());
        type.setDefaultAnnualDays(dto.getDefaultAnnualDays());
        type.setRequiresJustification(dto.getRequiresJustification());
        type.setCarryoverAllowed(dto.getCarryoverAllowed());

        if (dto.getConventionId() != null) {
            Convention convention = conventionRepository.findById(dto.getConventionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Convention not found: " + dto.getConventionId()));
            type.setConvention(convention);
        }

        return leaveTypeRepository.save(type);
    }

    @Transactional
    public LeaveType update(UUID id, CreateLeaveTypeDTO dto) {
        LeaveType type = findById(id);

        // RG-M2-11: code uniqueness check (exclude self)
        leaveTypeRepository.findByCode(dto.getCode().toUpperCase())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new BusinessException("DUPLICATE_CODE",
                            "Code '" + dto.getCode() + "' is already used by another leave type");
                });

        type.setName(dto.getName());
        type.setCode(dto.getCode().toUpperCase());
        type.setDefaultAnnualDays(dto.getDefaultAnnualDays());
        type.setRequiresJustification(dto.getRequiresJustification());
        type.setCarryoverAllowed(dto.getCarryoverAllowed());

        if (dto.getConventionId() != null) {
            Convention convention = conventionRepository.findById(dto.getConventionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Convention not found: " + dto.getConventionId()));
            type.setConvention(convention);
        } else {
            type.setConvention(null);
        }

        return leaveTypeRepository.save(type);
    }

    @Transactional(readOnly = true)
    public LeaveType findById(UUID id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<LeaveType> findAll() {
        return leaveTypeRepository.findAll();
    }

    @Transactional
    public void delete(UUID id) {
        LeaveType type = findById(id);
        leaveTypeRepository.delete(type);
    }
}
