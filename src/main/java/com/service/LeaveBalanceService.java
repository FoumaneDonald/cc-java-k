package com.service;

import com.entities.LeaveBalance;
import com.entities.LeaveType;
import com.exception.BusinessException;
import com.exception.ResourceNotFoundException;
import com.repository.LeaveBalanceRepository;
import com.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaveBalanceService {

	@Autowired private LeaveBalanceRepository leaveBalanceRepository;
	@Autowired private LeaveTypeRepository leaveTypeRepository;

    // RG-M2-01: verify sufficient balance before submission
    public void assertSufficientBalance(UUID employeeId, UUID leaveTypeId, int requestedDays) {
        int year = LocalDate.now().getYear();
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .orElseThrow(() -> new BusinessException("NO_BALANCE",
                        "No leave balance found for this leave type in " + year));

        if (balance.getRemainingDays().compareTo(BigDecimal.valueOf(requestedDays)) < 0) {
            throw new BusinessException("INSUFFICIENT_BALANCE",
                    "Insufficient balance. Requested: " + requestedDays +
                    " day(s), Available: " + balance.getRemainingDays());
        }
    }

    // RG-M2-09: debit balance on CONFIRMED
    @Transactional
    public void debit(UUID employeeId, UUID leaveTypeId, int days) {
        int year = LocalDate.now().getYear();
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .orElseThrow(() -> new BusinessException("NO_BALANCE",
                        "No leave balance found to debit"));

        BigDecimal newUsed = balance.getUsedDays().add(BigDecimal.valueOf(days));
        if (newUsed.compareTo(balance.getTotalDays()) > 0) {
            throw new BusinessException("INSUFFICIENT_BALANCE", "Balance would go negative after debit");
        }
        balance.setUsedDays(newUsed);
        leaveBalanceRepository.save(balance);
    }

    // RG-M2-09: re-credit on cancellation after confirmation
    @Transactional
    public void credit(UUID employeeId, UUID leaveTypeId, int days) {
        int year = LocalDate.now().getYear();
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .orElseThrow(() -> new BusinessException("NO_BALANCE",
                        "No leave balance found to credit"));

        BigDecimal newUsed = balance.getUsedDays().subtract(BigDecimal.valueOf(days));
        balance.setUsedDays(newUsed.max(BigDecimal.ZERO));
        leaveBalanceRepository.save(balance);
    }

    // RG-M2-15: initialize balance for new employee (pro-rata)
    @Transactional
    public LeaveBalance initializeBalance(UUID employeeId, UUID leaveTypeId, LocalDate hireDate) {
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType not found: " + leaveTypeId));

        int year = hireDate.getYear();
        int totalMonths = 12;
        int remainingMonths = 13 - hireDate.getMonthValue(); // months from hire month to Dec
        BigDecimal proRata = leaveType.getDefaultAnnualDays()
                .multiply(BigDecimal.valueOf(remainingMonths))
                .divide(BigDecimal.valueOf(totalMonths), 1, RoundingMode.HALF_UP);

        LeaveBalance balance = new LeaveBalance();
        balance.setEmployeeId(employeeId);
        balance.setLeaveType(leaveType);
        balance.setYear(year);
        balance.setTotalDays(proRata);
        balance.setUsedDays(BigDecimal.ZERO);
        return leaveBalanceRepository.save(balance);
    }

    @Transactional
    public LeaveBalance createOrUpdateBalance(UUID employeeId, UUID leaveTypeId, int year, BigDecimal totalDays) {
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .orElseGet(() -> {
                    LeaveType lt = leaveTypeRepository.findById(leaveTypeId)
                            .orElseThrow(() -> new ResourceNotFoundException("LeaveType not found: " + leaveTypeId));
                    LeaveBalance b = new LeaveBalance();
                    b.setEmployeeId(employeeId);
                    b.setLeaveType(lt);
                    b.setYear(year);
                    b.setUsedDays(BigDecimal.ZERO);
                    return b;
                });
        balance.setTotalDays(totalDays);
        return leaveBalanceRepository.save(balance);
    }

    @Transactional(readOnly = true)
    public List<LeaveBalance> getBalancesForEmployee(UUID employeeId) {
        return leaveBalanceRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<LeaveBalance> getBalancesForEmployeeAndYear(UUID employeeId, int year) {
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year);
    }
}
