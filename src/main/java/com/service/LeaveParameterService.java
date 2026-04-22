package com.service;

import com.dto.request.LeaveParameterDTO;
import com.entities.LeaveParameter;
import com.exception.BusinessException;
import com.exception.ResourceNotFoundException;
import com.repository.LeaveParameterRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaveParameterService {

	@Autowired private LeaveParameterRepository leaveParameterRepository;

    // Creates a new active config and deactivates the previous one
    @Transactional
    public LeaveParameter create(LeaveParameterDTO dto) {
        // Deactivate any existing active config
        leaveParameterRepository.findByActiveTrue().ifPresent(existing -> {
            existing.setActive(false);
            leaveParameterRepository.save(existing);
        });

        LeaveParameter param = new LeaveParameter();
        param.setCalculationMode(dto.getCalculationMode());
        param.setMinNoticeDays(dto.getMinNoticeDays());
        param.setPublicHolidays(dto.getPublicHolidays() != null
                ? new ArrayList<>(dto.getPublicHolidays())
                : new ArrayList<>());
        param.setActive(true);
        return leaveParameterRepository.save(param);
    }

    @Transactional
    public LeaveParameter update(UUID id, LeaveParameterDTO dto) {
        LeaveParameter param = findById(id);

        if (!param.getActive()) {
            throw new BusinessException("INACTIVE_PARAMETER",
                    "Cannot update an inactive parameter configuration. Create a new one instead.");
        }

        param.setCalculationMode(dto.getCalculationMode());
        param.setMinNoticeDays(dto.getMinNoticeDays());
        param.setPublicHolidays(dto.getPublicHolidays() != null
                ? new ArrayList<>(dto.getPublicHolidays())
                : new ArrayList<>());
        return leaveParameterRepository.save(param);
    }

    @Transactional(readOnly = true)
    public LeaveParameter findActive() {
        return leaveParameterRepository.findByActiveTrue()
                .orElseThrow(() -> new BusinessException("NO_ACTIVE_PARAMETER",
                        "No active leave parameter configuration found"));
    }

    @Transactional(readOnly = true)
    public LeaveParameter findById(UUID id) {
        return leaveParameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveParameter not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<LeaveParameter> findAll() {
        return leaveParameterRepository.findAll();
    }
}
