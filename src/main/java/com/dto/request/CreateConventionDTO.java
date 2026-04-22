package com.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Corps de requête pour créer ou mettre à jour une convention collective")
public class CreateConventionDTO {

    @NotBlank(message = "name is required")
    @Schema(description = "Nom de la convention collective", example = "Convention Collective Nationale")
    private String name;

    @Schema(description = "Nombre de jours annuels surchargé (remplace la valeur par défaut du type de congé — RG-M2-12)", nullable = true, example = "30")
    private Integer overrideAnnualDays;

    @Schema(description = "Délai minimum de prévenance en jours (surcharge les paramètres globaux)", nullable = true, example = "3")
    private Integer minNoticeDays;

    @Schema(description = "Conditions d'éligibilité à cette convention", nullable = true, example = "CDI après 6 mois d'ancienneté")
    private String eligibilityConditions;

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
