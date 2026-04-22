package com.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.enums.LeaveRequestStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_requests")
@Getter @Setter @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID employeeId;                // RG: plain UUID, no cross-module join

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;            // RG-M2-02: obligatoire

    @Column(nullable = false)
    private LocalDate endDate;              // RG-M2-02: fin >= début

    @Column(nullable = false)
    private Integer workingDays;            // RG-M2-03: calculé automatiquement

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveRequestStatus status = LeaveRequestStatus.DRAFT;

    private String rejectionReason;         // RG-M2-08: motif obligatoire si rejet

    private String justificationPath;       // RG-M2-11: si justificatif requis

    // RG-M2-07: two approvers tracked separately
    private UUID validatedByChefId;         // Chef de Service
    private LocalDateTime validatedAt;

    private UUID confirmedByRhId;           // RH Manager
    private LocalDateTime confirmedAt;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(UUID employeeId) {
		this.employeeId = employeeId;
	}

	public LeaveType getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(LeaveType leaveType) {
		this.leaveType = leaveType;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Integer getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(Integer workingDays) {
		this.workingDays = workingDays;
	}

	public LeaveRequestStatus getStatus() {
		return status;
	}

	public void setStatus(LeaveRequestStatus status) {
		this.status = status;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public String getJustificationPath() {
		return justificationPath;
	}

	public void setJustificationPath(String justificationPath) {
		this.justificationPath = justificationPath;
	}

	public UUID getValidatedByChefId() {
		return validatedByChefId;
	}

	public void setValidatedByChefId(UUID validatedByChefId) {
		this.validatedByChefId = validatedByChefId;
	}

	public LocalDateTime getValidatedAt() {
		return validatedAt;
	}

	public void setValidatedAt(LocalDateTime validatedAt) {
		this.validatedAt = validatedAt;
	}

	public UUID getConfirmedByRhId() {
		return confirmedByRhId;
	}

	public void setConfirmedByRhId(UUID confirmedByRhId) {
		this.confirmedByRhId = confirmedByRhId;
	}

	public LocalDateTime getConfirmedAt() {
		return confirmedAt;
	}

	public void setConfirmedAt(LocalDateTime confirmedAt) {
		this.confirmedAt = confirmedAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
    
}
