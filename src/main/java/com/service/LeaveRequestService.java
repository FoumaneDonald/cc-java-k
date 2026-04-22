package com.service;

import com.entities.LeaveRequest;
import com.entities.LeaveType;
import com.enums.LeaveRequestStatus;
import com.event.LeaveStatusChangedEvent;
import com.exception.BusinessException;
import com.exception.InvalidStatusTransitionException;
import com.exception.ResourceNotFoundException;
import com.repository.LeaveRequestRepository;
import com.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

	@Autowired private LeaveRequestRepository leaveRequestRepository;
	@Autowired private LeaveTypeRepository leaveTypeRepository;
	@Autowired private LeaveBalanceService leaveBalanceService;
	@Autowired private WorkingDaysCalculatorService calculatorService;
	@Autowired private ApplicationEventPublisher eventPublisher;

    // UC-M2-01: employee creates a leave request in DRAFT
    @Transactional
    public LeaveRequest create(UUID employeeId, UUID leaveTypeId,
                               LocalDate startDate, LocalDate endDate,
                               String justificationPath) {
        // RG-M2-02: dates required, end >= start
        if (endDate.isBefore(startDate)) {
            throw new BusinessException("INVALID_DATES", "endDate must be >= startDate");
        }

        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType not found: " + leaveTypeId));

        // RG-M2-03: compute working days
        int workingDays = calculatorService.calculate(startDate, endDate);

        LeaveRequest request = new LeaveRequest();
        request.setEmployeeId(employeeId);
        request.setLeaveType(leaveType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setWorkingDays(workingDays);
        request.setStatus(LeaveRequestStatus.DRAFT);
        request.setJustificationPath(justificationPath);

        return leaveRequestRepository.save(request);
    }

    // UC-M2-01 step 5: employee submits the draft
    @Transactional
    public LeaveRequest submit(UUID requestId) {
        LeaveRequest request = findById(requestId);

        // RG-M2-05: only DRAFT can be submitted
        if (request.getStatus() != LeaveRequestStatus.DRAFT) {
            throw new InvalidStatusTransitionException(
                    request.getStatus().name(), LeaveRequestStatus.SUBMITTED.name());
        }

        // RG-M2-14: minimum notice check
        calculatorService.validateNotice(request.getStartDate());

        // RG-M2-01: balance check
        leaveBalanceService.assertSufficientBalance(
                request.getEmployeeId(),
                request.getLeaveType().getId(),
                request.getWorkingDays());

        // RG-M2-04: overlap check
        assertNoOverlap(request.getEmployeeId(), request.getStartDate(),
                request.getEndDate(), request.getId());

        LeaveRequestStatus previous = request.getStatus();
        request.setStatus(LeaveRequestStatus.SUBMITTED);
        LeaveRequest saved = leaveRequestRepository.save(request);

        // RG-M2-10: notify Chef de Service
        publishEvent(saved, previous);
        return saved;
    }

    // UC-M2-02: Chef de Service validates (SUBMITTED → VALIDATED)
    @Transactional
    public LeaveRequest validate(UUID requestId, UUID chefId) {
        LeaveRequest request = findById(requestId);

        if (request.getStatus() != LeaveRequestStatus.SUBMITTED) {
            throw new InvalidStatusTransitionException(
                    request.getStatus().name(), LeaveRequestStatus.VALIDATED.name());
        }

        LeaveRequestStatus previous = request.getStatus();
        request.setStatus(LeaveRequestStatus.VALIDATED);
        request.setValidatedByChefId(chefId);
        request.setValidatedAt(LocalDateTime.now());
        LeaveRequest saved = leaveRequestRepository.save(request);

        publishEvent(saved, previous);
        return saved;
    }

    // UC-M2-03: RH Manager confirms (VALIDATED → CONFIRMED)
    @Transactional
    public LeaveRequest confirm(UUID requestId, UUID rhId) {
        LeaveRequest request = findById(requestId);

        if (request.getStatus() != LeaveRequestStatus.VALIDATED) {
            throw new InvalidStatusTransitionException(
                    request.getStatus().name(), LeaveRequestStatus.CONFIRMED.name());
        }

        LeaveRequestStatus previous = request.getStatus();
        request.setStatus(LeaveRequestStatus.CONFIRMED);
        request.setConfirmedByRhId(rhId);
        request.setConfirmedAt(LocalDateTime.now());
        LeaveRequest saved = leaveRequestRepository.save(request);

        // RG-M2-09: debit balance only on CONFIRMED
        leaveBalanceService.debit(
                saved.getEmployeeId(),
                saved.getLeaveType().getId(),
                saved.getWorkingDays());

        publishEvent(saved, previous);
        return saved;
    }

    // UC-M2-02 / UC-M2-03: rejection by Chef or RH
    @Transactional
    public LeaveRequest reject(UUID requestId, UUID rejectorId, String reason) {
        LeaveRequest request = findById(requestId);

        // RG-M2-08: reason mandatory
        if (reason == null || reason.isBlank()) {
            throw new BusinessException("REJECTION_REASON_REQUIRED",
                    "A rejection reason is mandatory");
        }

        // Can reject from SUBMITTED (Chef) or VALIDATED (RH)
        if (request.getStatus() != LeaveRequestStatus.SUBMITTED
                && request.getStatus() != LeaveRequestStatus.VALIDATED) {
            throw new InvalidStatusTransitionException(
                    request.getStatus().name(), LeaveRequestStatus.REJECTED.name());
        }

        LeaveRequestStatus previous = request.getStatus();
        request.setStatus(LeaveRequestStatus.REJECTED);
        request.setRejectionReason(reason);
        LeaveRequest saved = leaveRequestRepository.save(request);

        // RG-M2-08: balance NOT debited on rejection
        publishEvent(saved, previous);
        return saved;
    }

    // RG-M2-06: employee can cancel SUBMITTED; RH can cancel VALIDATED/CONFIRMED
    @Transactional
    public LeaveRequest cancel(UUID requestId) {
        LeaveRequest request = findById(requestId);

        boolean cancellable = request.getStatus() == LeaveRequestStatus.DRAFT
                || request.getStatus() == LeaveRequestStatus.SUBMITTED
                || request.getStatus() == LeaveRequestStatus.VALIDATED
                || request.getStatus() == LeaveRequestStatus.CONFIRMED;

        if (!cancellable) {
            throw new BusinessException("CANNOT_CANCEL",
                    "A " + request.getStatus() + " request cannot be cancelled");
        }

        boolean wasConfirmed = request.getStatus() == LeaveRequestStatus.CONFIRMED;
        LeaveRequestStatus previous = request.getStatus();

        request.setStatus(LeaveRequestStatus.REJECTED);
        request.setRejectionReason("Cancelled");
        LeaveRequest saved = leaveRequestRepository.save(request);

        // RG-M2-09: re-credit if it was already confirmed
        if (wasConfirmed) {
            leaveBalanceService.credit(
                    saved.getEmployeeId(),
                    saved.getLeaveType().getId(),
                    saved.getWorkingDays());
        }

        publishEvent(saved, previous);
        return saved;
    }

    // RG-M2-05: update only allowed in DRAFT
    @Transactional
    public LeaveRequest update(UUID requestId, LocalDate startDate, LocalDate endDate,
                               String justificationPath) {
        LeaveRequest request = findById(requestId);

        if (request.getStatus() != LeaveRequestStatus.DRAFT) {
            throw new BusinessException("NOT_EDITABLE",
                    "Only DRAFT requests can be modified (RG-M2-05)");
        }

        if (endDate.isBefore(startDate)) {
            throw new BusinessException("INVALID_DATES", "endDate must be >= startDate");
        }

        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setWorkingDays(calculatorService.calculate(startDate, endDate));
        if (justificationPath != null) {
            request.setJustificationPath(justificationPath);
        }
        return leaveRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public LeaveRequest findById(UUID id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> findByEmployee(UUID employeeId) {
        return leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> findPendingValidation() {
        return leaveRequestRepository.findByStatusOrderByCreatedAtAsc(LeaveRequestStatus.SUBMITTED);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> findPendingConfirmation() {
        return leaveRequestRepository.findByStatusOrderByCreatedAtAsc(LeaveRequestStatus.VALIDATED);
    }

    // For M3 inter-module: absences in a payroll period
    @Transactional(readOnly = true)
    public List<LeaveRequest> findAbsencesInPeriod(UUID employeeId,
                                                    LocalDate periodStart,
                                                    LocalDate periodEnd) {
        return leaveRequestRepository.findConfirmedAbsencesInPeriod(
                employeeId, periodStart, periodEnd);
    }

    // RG-M2-04: internal overlap check
    private void assertNoOverlap(UUID employeeId, LocalDate start, LocalDate end, UUID excludeId) {
        List<LeaveRequest> overlapping =
                leaveRequestRepository.findOverlapping(employeeId, start, end, excludeId);
        if (!overlapping.isEmpty()) {
            throw new BusinessException("OVERLAP_DETECTED",
                    "This request overlaps with an existing active request (id: "
                    + overlapping.get(0).getId() + ")");
        }
    }

    private void publishEvent(LeaveRequest request, LeaveRequestStatus previous) {
        eventPublisher.publishEvent(new LeaveStatusChangedEvent(
                this,
                request.getId(),
                request.getEmployeeId(),
                previous,
                request.getStatus(),
                request.getRejectionReason()));
    }
}
