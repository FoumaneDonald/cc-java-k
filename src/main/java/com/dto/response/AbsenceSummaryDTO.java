package com.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AbsenceSummaryDTO {
    private UUID leaveRequestId;
    private UUID employeeId;
    private String leaveTypeCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workingDays;
	public UUID getLeaveRequestId() {
		return leaveRequestId;
	}
	public void setLeaveRequestId(UUID leaveRequestId) {
		this.leaveRequestId = leaveRequestId;
	}
	public UUID getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(UUID employeeId) {
		this.employeeId = employeeId;
	}
	public String getLeaveTypeCode() {
		return leaveTypeCode;
	}
	public void setLeaveTypeCode(String leaveTypeCode) {
		this.leaveTypeCode = leaveTypeCode;
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
    
    
}
