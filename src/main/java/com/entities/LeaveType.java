package com.entities;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leave_types")
@Getter @Setter @NoArgsConstructor
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "default_annual_balance", nullable = false, precision = 5, scale = 2)
    private BigDecimal defaultAnnualDays;

    @Column(name = "requires_justification", nullable = false)
    private Boolean requiresJustification;

    @Column(name = "is_carry_over_allowed", nullable = false)
    private Boolean carryoverAllowed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convention_id")
    private Convention convention;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getDefaultAnnualDays() {
		return defaultAnnualDays;
	}

	public void setDefaultAnnualDays(BigDecimal defaultAnnualDays) {
		this.defaultAnnualDays = defaultAnnualDays;
	}

	public Boolean getRequiresJustification() {
		return requiresJustification;
	}

	public void setRequiresJustification(Boolean requiresJustification) {
		this.requiresJustification = requiresJustification;
	}

	public Boolean getCarryoverAllowed() {
		return carryoverAllowed;
	}

	public void setCarryoverAllowed(Boolean carryoverAllowed) {
		this.carryoverAllowed = carryoverAllowed;
	}

	public Convention getConvention() {
		return convention;
	}

	public void setConvention(Convention convention) {
		this.convention = convention;
	}  
    
    
}
