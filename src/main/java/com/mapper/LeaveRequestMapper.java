package com.mapper;

import com.dto.response.*;
import com.entities.*;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestMapper {

    public LeaveRequestResponseDTO toResponse(LeaveRequest r) {
        LeaveRequestResponseDTO dto = new LeaveRequestResponseDTO();
        dto.setId(r.getId());
        dto.setEmployeeId(r.getEmployeeId());
        dto.setLeaveType(toTypeSummary(r.getLeaveType()));
        dto.setStartDate(r.getStartDate());
        dto.setEndDate(r.getEndDate());
        dto.setWorkingDays(r.getWorkingDays());
        dto.setStatus(r.getStatus());
        dto.setRejectionReason(r.getRejectionReason());
        dto.setJustificationPath(r.getJustificationPath());
        dto.setValidatedByChefId(r.getValidatedByChefId());
        dto.setValidatedAt(r.getValidatedAt());
        dto.setConfirmedByRhId(r.getConfirmedByRhId());
        dto.setConfirmedAt(r.getConfirmedAt());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }

    public LeaveTypeSummaryDTO toTypeSummary(LeaveType t) {
        LeaveTypeSummaryDTO dto = new LeaveTypeSummaryDTO();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setCode(t.getCode());
        return dto;
    }

    public LeaveTypeResponseDTO toTypeResponse(LeaveType t) {
        LeaveTypeResponseDTO dto = new LeaveTypeResponseDTO();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setCode(t.getCode());
        dto.setDefaultAnnualDays(t.getDefaultAnnualDays());
        dto.setRequiresJustification(t.getRequiresJustification());
        dto.setCarryoverAllowed(t.getCarryoverAllowed());
        if (t.getConvention() != null) {
            dto.setConvention(toConventionResponse(t.getConvention()));
        }
        return dto;
    }

    public ConventionResponseDTO toConventionResponse(Convention c) {
        ConventionResponseDTO dto = new ConventionResponseDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setOverrideAnnualDays(c.getOverrideAnnualDays());
        dto.setMinNoticeDays(c.getMinNoticeDays());
        dto.setEligibilityConditions(c.getEligibilityConditions());
        return dto;
    }

    public LeaveBalanceResponseDTO toBalanceResponse(LeaveBalance b) {
        LeaveBalanceResponseDTO dto = new LeaveBalanceResponseDTO();
        dto.setId(b.getId());
        dto.setEmployeeId(b.getEmployeeId());
        dto.setLeaveType(toTypeSummary(b.getLeaveType()));
        dto.setYear(b.getYear());
        dto.setTotalDays(b.getTotalDays());
        dto.setUsedDays(b.getUsedDays());
        dto.setRemainingDays(b.getRemainingDays());
        return dto;
    }

    public LeaveParameterResponseDTO toParameterResponse(com.entities.LeaveParameter p) {
        LeaveParameterResponseDTO dto = new LeaveParameterResponseDTO();
        dto.setId(p.getId());
        dto.setCalculationMode(p.getCalculationMode());
        dto.setMinNoticeDays(p.getMinNoticeDays());
        dto.setPublicHolidays(p.getPublicHolidays());
        dto.setActive(p.getActive());
        return dto;
    }

    public AbsenceSummaryDTO toAbsenceSummary(LeaveRequest r) {
        AbsenceSummaryDTO dto = new AbsenceSummaryDTO();
        dto.setLeaveRequestId(r.getId());
        dto.setEmployeeId(r.getEmployeeId());
        dto.setLeaveTypeCode(r.getLeaveType().getCode());
        dto.setStartDate(r.getStartDate());
        dto.setEndDate(r.getEndDate());
        dto.setWorkingDays(r.getWorkingDays());
        return dto;
    }
}
