package com.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Corps de requête pour créer ou mettre à jour un type de congé")
public class CreateLeaveTypeDTO {

    @NotBlank(message = "name is required")
    @Schema(description = "Libellé complet du type de congé", example = "Congés Payés")
    private String name;

    @NotBlank(message = "code is required")
    @Schema(description = "Code unique (automatiquement mis en majuscules — RG-M2-11)", example = "CP")
    private String code;

    @NotNull(message = "defaultAnnualDays is required")
    @Positive(message = "defaultAnnualDays must be positive")
    @Schema(description = "Solde annuel par défaut en jours (peut être surchargé par la convention)", example = "30")
    private BigDecimal defaultAnnualDays;

    @NotNull(message = "requiresJustification is required")
    @Schema(description = "Si true, l'employé doit fournir un justificatif à la soumission", example = "false")
    private Boolean requiresJustification;

    @NotNull(message = "carryoverAllowed is required")
    @Schema(description = "Si true, le solde non utilisé est reportable à l'année suivante", example = "true")
    private Boolean carryoverAllowed;

    @Schema(description = "UUID de la convention collective associée (optionnel)", nullable = true)
    private UUID conventionId;

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

	public UUID getConventionId() {
		return conventionId;
	}

	public void setConventionId(UUID conventionId) {
		this.conventionId = conventionId;
	}
    
    
}
