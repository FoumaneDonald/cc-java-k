package com.dto.request;

import com.enums.DayCalculationMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "Configuration des paramètres de congé — devient la configuration active à la création")
public class LeaveParameterDTO {

    @NotNull(message = "calculationMode is required")
    @Schema(description = "Mode de calcul des jours : WORKING_DAYS (exclut week-ends et jours fériés) ou CALENDAR_DAYS", example = "WORKING_DAYS")
    private DayCalculationMode calculationMode;

    @NotNull(message = "minNoticeDays is required")
    @PositiveOrZero(message = "minNoticeDays must be 0 or greater")
    @Schema(description = "Nombre minimum de jours de prévenance avant la date de début (RG-M2-14)", example = "2")
    private Integer minNoticeDays;

    @Schema(description = "Liste des jours fériés à exclure du calcul (format yyyy-MM-dd)",
            example = "[\"2025-01-01\",\"2025-05-01\",\"2025-12-25\"]")
    private List<LocalDate> publicHolidays;

	public DayCalculationMode getCalculationMode() {
		return calculationMode;
	}

	public void setCalculationMode(DayCalculationMode calculationMode) {
		this.calculationMode = calculationMode;
	}

	public Integer getMinNoticeDays() {
		return minNoticeDays;
	}

	public void setMinNoticeDays(Integer minNoticeDays) {
		this.minNoticeDays = minNoticeDays;
	}

	public List<LocalDate> getPublicHolidays() {
		return publicHolidays;
	}

	public void setPublicHolidays(List<LocalDate> publicHolidays) {
		this.publicHolidays = publicHolidays;
	}
    
    
}
