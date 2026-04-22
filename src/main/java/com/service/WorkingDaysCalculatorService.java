package com.service;

import com.entities.LeaveParameter;
import com.enums.DayCalculationMode;
import com.exception.BusinessException;
import com.repository.LeaveParameterRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WorkingDaysCalculatorService {

	@Autowired private LeaveParameterRepository leaveParameterRepository;

    // RG-M2-03: calculate working days excluding weekends and public holidays
    public int calculate(LocalDate startDate, LocalDate endDate) {
        LeaveParameter params = leaveParameterRepository.findByActiveTrue()
                .orElseThrow(() -> new BusinessException("NO_ACTIVE_PARAMETER",
                        "No active leave parameter configuration found"));

        if (params.getCalculationMode() == DayCalculationMode.CALENDAR_DAYS) {
            return (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
        }

        int count = 0;
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            boolean isWeekend = switch (current.getDayOfWeek()) {
                case SATURDAY, SUNDAY -> true;
                default -> false;
            };
            boolean isHoliday = params.getPublicHolidays().contains(current);
            if (!isWeekend && !isHoliday) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }

    // RG-M2-14: check minimum notice days
    public void validateNotice(LocalDate startDate) {
        LeaveParameter params = leaveParameterRepository.findByActiveTrue()
                .orElseThrow(() -> new BusinessException("NO_ACTIVE_PARAMETER",
                        "No active leave parameter configuration found"));

        LocalDate minAllowedStart = LocalDate.now().plusDays(params.getMinNoticeDays());
        if (startDate.isBefore(minAllowedStart)) {
            throw new BusinessException("INSUFFICIENT_NOTICE",
                    "Leave request must be submitted at least " +
                    params.getMinNoticeDays() + " day(s) in advance. " +
                    "Earliest allowed start date: " + minAllowedStart);
        }
    }
}
