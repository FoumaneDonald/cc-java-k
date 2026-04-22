package com.entities;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conventions")
@Getter @Setter @NoArgsConstructor
public class Convention {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    // RG-M2-12: peut surcharger solde, délai de prévenance, conditions éligibilité
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
