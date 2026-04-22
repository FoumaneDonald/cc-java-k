package com.service;

import com.entities.LeaveParameter;
import com.enums.DayCalculationMode;
import com.repository.LeaveParameterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkingDaysCalculatorServiceTest {

    @Mock
    private LeaveParameterRepository leaveParameterRepository;

    @InjectMocks
    private WorkingDaysCalculatorService calculator;

    private LeaveParameter params;

    @BeforeEach
    void setUp() {
        params = new LeaveParameter();
        params.setCalculationMode(DayCalculationMode.WORKING_DAYS);
        params.setMinNoticeDays(2);
        params.setPublicHolidays(List.of());
        params.setActive(true);
        when(leaveParameterRepository.findByActiveTrue()).thenReturn(Optional.of(params));
    }

    @Test
    void shouldCountFiveWorkingDaysForOneWeek() {
        // Monday to Friday
        LocalDate start = LocalDate.of(2025, 4, 7);
        LocalDate end   = LocalDate.of(2025, 4, 11);
        assertThat(calculator.calculate(start, end)).isEqualTo(5);
    }

    @Test
    void shouldExcludeWeekends() {
        // Monday to Sunday = 5 working days
        LocalDate start = LocalDate.of(2025, 4, 7);
        LocalDate end   = LocalDate.of(2025, 4, 13);
        assertThat(calculator.calculate(start, end)).isEqualTo(5);
    }

    @Test
    void shouldExcludePublicHoliday() {
        LocalDate holiday = LocalDate.of(2025, 4, 9); // Wednesday
        params.setPublicHolidays(List.of(holiday));

        LocalDate start = LocalDate.of(2025, 4, 7);
        LocalDate end   = LocalDate.of(2025, 4, 11);
        // Monday–Friday minus the holiday = 4
        assertThat(calculator.calculate(start, end)).isEqualTo(4);
    }

    @Test
    void shouldReturnOneForSingleDay() {
        LocalDate day = LocalDate.of(2025, 4, 7); // Monday
        assertThat(calculator.calculate(day, day)).isEqualTo(1);
    }

    @Test
    void shouldCountCalendarDaysWhenModeIsCalendar() {
        params.setCalculationMode(DayCalculationMode.CALENDAR_DAYS);
        LocalDate start = LocalDate.of(2025, 4, 7);
        LocalDate end   = LocalDate.of(2025, 4, 13);
        assertThat(calculator.calculate(start, end)).isEqualTo(7);
    }
}
