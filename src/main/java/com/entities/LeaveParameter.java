package com.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.enums.DayCalculationMode;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_parameters")
@Getter @Setter @NoArgsConstructor
public class LeaveParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // RG-M2-14: jours fériés (stored as a list of dates)
    @ElementCollection
    @CollectionTable(name = "public_holidays",
                     joinColumns = @JoinColumn(name = "leave_parameter_id"))
    @Column(name = "holiday_date")
    private List<LocalDate> publicHolidays = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayCalculationMode calculationMode; // RG-M2-14: jours ouvrables vs calendaires

    @Column(nullable = false)
    private Integer minNoticeDays;              // RG-M2-14: délai minimum de prévenance

    @Column(nullable = false)
    private Boolean active = true;              // only one active config at a time

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public List<LocalDate> getPublicHolidays() {
		return publicHolidays;
	}

	public void setPublicHolidays(List<LocalDate> publicHolidays) {
		this.publicHolidays = publicHolidays;
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
    
    
}
