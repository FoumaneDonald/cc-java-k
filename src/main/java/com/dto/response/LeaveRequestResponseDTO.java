package com.dto.response;

import com.enums.LeaveRequestStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LeaveRequestResponseDTO {
    private UUID id;
    private UUID employeeId;
    private LeaveTypeSummaryDTO leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workingDays;
    private LeaveRequestStatus status;
    private String rejectionReason;
    private String justificationPath;
    private UUID validatedByChefId;
    private LocalDateTime validatedAt;
    private UUID confirmedByRhId;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;
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
	public LeaveTypeSummaryDTO getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(LeaveTypeSummaryDTO leaveType) {
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
