package com.repository;

import com.entities.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, UUID> {

    // RG-M2-01: check balance before submission
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(
            UUID employeeId, UUID leaveTypeId, int year);

    List<LeaveBalance> findByEmployeeIdAndYear(UUID employeeId, int year);

    List<LeaveBalance> findByEmployeeId(UUID employeeId);
}
