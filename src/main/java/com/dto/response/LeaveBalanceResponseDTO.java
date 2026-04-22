package com.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LeaveBalanceResponseDTO {
    private UUID id;
    private UUID employeeId;
    private LeaveTypeSummaryDTO leaveType;
    private Integer year;
    private BigDecimal totalDays;
    private BigDecimal usedDays;
    private BigDecimal remainingDays;
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
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public BigDecimal getTotalDays() {
		return totalDays;
	}
	public void setTotalDays(BigDecimal totalDays) {
		this.totalDays = totalDays;
	}
	public BigDecimal getUsedDays() {
		return usedDays;
	}
	public void setUsedDays(BigDecimal usedDays) {
		this.usedDays = usedDays;
	}
	public BigDecimal getRemainingDays() {
		return remainingDays;
	}
	public void setRemainingDays(BigDecimal remainingDays) {
		this.remainingDays = remainingDays;
	}
    
    
}
