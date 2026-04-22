package com.dto.response;

import com.enums.DayCalculationMode;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class LeaveParameterResponseDTO {
    private UUID id;
    private DayCalculationMode calculationMode;
    private Integer minNoticeDays;
    private List<LocalDate> publicHolidays;
    private Boolean active;
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
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
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
    
    
}
