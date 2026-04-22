package com.service;

import com.entities.LeaveRequest;
import com.entities.LeaveType;
import com.enums.LeaveRequestStatus;
import com.exception.BusinessException;
import com.exception.InvalidStatusTransitionException;
import com.repository.LeaveRequestRepository;
import com.repository.LeaveTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceTest {

    @Mock private LeaveRequestRepository leaveRequestRepository;
    @Mock private LeaveTypeRepository leaveTypeRepository;
    @Mock private LeaveBalanceService leaveBalanceService;
    @Mock private WorkingDaysCalculatorService calculatorService;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private LeaveRequestService service;

    private LeaveType leaveType;
    private UUID employeeId;
    private UUID leaveTypeId;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        leaveTypeId = UUID.randomUUID();
        leaveType = new LeaveType();
        leaveType.setId(leaveTypeId);
        leaveType.setCode("CP");
        leaveType.setName("Congés Payés");
        leaveType.setDefaultAnnualDays(new BigDecimal("25.00"));
        leaveType.setRequiresJustification(false);
        leaveType.setCarryoverAllowed(true);
    }

    // RG-M2-02
    @Test
    void create_shouldThrow_whenEndBeforeStart() {
        assertThatThrownBy(() -> service.create(
                employeeId, leaveTypeId,
                LocalDate.of(2025, 5, 10),
                LocalDate.of(2025, 5, 5),
                null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("endDate must be >= startDate");
    }

    // RG-M2-05
    @Test
    void submit_shouldThrow_whenNotDraft() {
        LeaveRequest request = buildRequest(LeaveRequestStatus.SUBMITTED);
        when(leaveRequestRepository.findById(any())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.submit(request.getId()))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    // RG-M2-04
    @Test
    void submit_shouldThrow_whenOverlapDetected() {
        LeaveRequest request = buildRequest(LeaveRequestStatus.DRAFT);
        when(leaveRequestRepository.findById(any())).thenReturn(Optional.of(request));
        doNothing().when(calculatorService).validateNotice(any());
        doNothing().when(leaveBalanceService).assertSufficientBalance(any(), any(), anyInt());
        when(leaveRequestRepository.findOverlapping(any(), any(), any(), any()))
                .thenReturn(List.of(buildRequest(LeaveRequestStatus.CONFIRMED)));

        assertThatThrownBy(() -> service.submit(request.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("overlap");
    }

    // RG-M2-07: Chef validates — must be SUBMITTED
    @Test
    void validate_shouldThrow_whenNotSubmitted() {
        LeaveRequest request = buildRequest(LeaveRequestStatus.DRAFT);
        when(leaveRequestRepository.findById(any())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.validate(request.getId(), UUID.randomUUID()))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    // RG-M2-07: RH confirms — must be VALIDATED
    @Test
    void confirm_shouldThrow_whenNotValidated() {
        LeaveRequest request = buildRequest(LeaveRequestStatus.SUBMITTED);
        when(leaveRequestRepository.findById(any())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.confirm(request.getId(), UUID.randomUUID()))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    // RG-M2-08
    @Test
    void reject_shouldThrow_whenReasonIsBlank() {
        LeaveRequest request = buildRequest(LeaveRequestStatus.SUBMITTED);
        when(leaveRequestRepository.findById(any())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.reject(request.getId(), UUID.randomUUID(), "  "))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("rejection reason");
    }

    // RG-M2-09: balance debited only on confirm
    @Test
    void confirm_shouldDebitBalance() {
        LeaveRequest request = buildRequest(LeaveRequestStatus.VALIDATED);
        request.setWorkingDays(5);
        when(leaveRequestRepository.findById(any())).thenReturn(Optional.of(request));
        when(leaveRequestRepository.save(any())).thenReturn(request);

        service.confirm(request.getId(), UUID.randomUUID());

        verify(leaveBalanceService).debit(
                request.getEmployeeId(),
                request.getLeaveType().getId(),
                5);
    }

    // RG-M2-09: balance recredited on cancel-after-confirm
    @Test
    void cancel_shouldCreditBalance_whenWasConfirmed() {
        LeaveRequest request = buildRequest(LeaveRequestStatus.CONFIRMED);
        request.setWorkingDays(3);
        when(leaveRequestRepository.findById(any())).thenReturn(Optional.of(request));
        when(leaveRequestRepository.save(any())).thenReturn(request);

        service.cancel(request.getId());

        verify(leaveBalanceService).credit(
                request.getEmployeeId(),
                request.getLeaveType().getId(),
                3);
    }

    private LeaveRequest buildRequest(LeaveRequestStatus status) {
        LeaveRequest r = new LeaveRequest();
        r.setId(UUID.randomUUID());
        r.setEmployeeId(employeeId);
        r.setLeaveType(leaveType);
        r.setStartDate(LocalDate.of(2025, 6, 1));
        r.setEndDate(LocalDate.of(2025, 6, 5));
        r.setWorkingDays(5);
        r.setStatus(status);
        return r;
    }
}
