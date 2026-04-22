package com.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LeaveTypeResponseDTO {
    private UUID id;
    private String name;
    private String code;
    private BigDecimal defaultAnnualDays;
    private Boolean requiresJustification;
    private Boolean carryoverAllowed;
    private ConventionResponseDTO convention;
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
	public ConventionResponseDTO getConvention() {
		return convention;
	}
	public void setConvention(ConventionResponseDTO convention) {
		this.convention = convention;
	}
    
    
}
