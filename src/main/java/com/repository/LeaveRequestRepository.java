package com.repository;

import com.entities.LeaveRequest;
import com.enums.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {

    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);

    List<LeaveRequest> findByStatus(LeaveRequestStatus status);

    // RG-M2-04: overlap check — find any active requests in the same date range
    @Query("""
        SELECT r FROM LeaveRequest r
        WHERE r.employeeId = :employeeId
          AND r.status IN ('SUBMITTED','VALIDATED','CONFIRMED')
          AND r.startDate <= :endDate
          AND r.endDate   >= :startDate
          AND (:excludeId IS NULL OR r.id <> :excludeId)
        """)
    List<LeaveRequest> findOverlapping(
            @Param("employeeId") UUID employeeId,
            @Param("startDate")  LocalDate startDate,
            @Param("endDate")    LocalDate endDate,
            @Param("excludeId")  UUID excludeId);

    // For M3 consumption: confirmed absences in a given month
    @Query("""
        SELECT r FROM LeaveRequest r
        WHERE r.employeeId = :employeeId
          AND r.status = 'CONFIRMED'
          AND r.startDate <= :periodEnd
          AND r.endDate   >= :periodStart
        """)
    List<LeaveRequest> findConfirmedAbsencesInPeriod(
            @Param("employeeId")   UUID employeeId,
            @Param("periodStart")  LocalDate periodStart,
            @Param("periodEnd")    LocalDate periodEnd);

    // Chef de service: pending requests for employees in a department
    @Query("""
        SELECT r FROM LeaveRequest r
        WHERE r.employeeId IN :employeeIds
          AND r.status = 'SUBMITTED'
        ORDER BY r.createdAt ASC
        """)
    List<LeaveRequest> findPendingForEmployees(@Param("employeeIds") List<UUID> employeeIds);

    // RH: requests awaiting final confirmation
    List<LeaveRequest> findByStatusOrderByCreatedAtAsc(LeaveRequestStatus status);
}
