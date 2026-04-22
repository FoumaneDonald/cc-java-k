package com.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class ConventionResponseDTO {
    private UUID id;
    private String name;
    private Integer overrideAnnualDays;
    private Integer minNoticeDays;
    private String eligibilityConditions;
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
	public Integer getOverrideAnnualDays() {
		return overrideAnnualDays;
	}
	public void setOverrideAnnualDays(Integer overrideAnnualDays) {
		this.overrideAnnualDays = overrideAnnualDays;
	}
	public Integer getMinNoticeDays() {
		return minNoticeDays;
	}
	public void setMinNoticeDays(Integer minNoticeDays) {
		this.minNoticeDays = minNoticeDays;
	}
	public String getEligibilityConditions() {
		return eligibilityConditions;
	}
	public void setEligibilityConditions(String eligibilityConditions) {
		this.eligibilityConditions = eligibilityConditions;
	}
    
    
}
