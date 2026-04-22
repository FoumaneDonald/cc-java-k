package com.entities;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_balances",
       uniqueConstraints = @UniqueConstraint(name = "uk_employee_leave_year", columnNames = {"employee_id", "leave_type_id", "balance_year"}))
@Getter @Setter @NoArgsConstructor
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;                // plain UUID — no cross-module JPA join

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "total_days", nullable = false, precision = 5, scale = 1)
    private BigDecimal totalDays;           // RG-M2-15: prorata pour nouveaux employés

    @Column(name = "used_days", nullable = false, precision = 5, scale = 1)
    private BigDecimal usedDays = BigDecimal.ZERO;

    public BigDecimal getRemainingDays() {
        return totalDays.subtract(usedDays); // RG-M2-01: solde suffisant
    }

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
    
    
}
