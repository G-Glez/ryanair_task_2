package com.ryanair.task2.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilTests {
    @DisplayName("Test for DateUtils.getMonthsBetween with same month and year")
    @Test
    void testGetMonthsBetweenWithSameMonthAndYear() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 31, 23, 59);

        List<LocalDate> months = DateUtils.getMonthsBetween(start, end);

        assertEquals(1, months.size());
        assertEquals(LocalDate.of(2023, 1, 1), months.getFirst());
    }

    @DisplayName("Test for DateUtils.getMonthsBetween with different months")
    @Test
    void testGetMonthsBetweenWithDifferentMonths() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 3, 31, 23, 59);

        List<LocalDate> months = DateUtils.getMonthsBetween(start, end);

        assertEquals(3, months.size());
        assertEquals(LocalDate.of(2023, 1, 1), months.getFirst());
        assertEquals(LocalDate.of(2023, 2, 1), months.get(1));
        assertEquals(LocalDate.of(2023, 3, 1), months.getLast());
    }

    @DisplayName("Test for DateUtils.getMonthsBetween with different years")
    @Test
    void testGetMonthsBetweenWithDifferentYears() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 0, 0);

        List<LocalDate> months = DateUtils.getMonthsBetween(start, end);

        assertEquals(13, months.size());

        for(int i = 2023; i <= 2024 && !months.isEmpty(); i++) {
            for(int j = 1; j <= 12 && !months.isEmpty(); j++) {
                assertEquals(LocalDate.of(i, j, 1), months.removeFirst());
            }
        }
    }
}
