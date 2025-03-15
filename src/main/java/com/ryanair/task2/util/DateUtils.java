package com.ryanair.task2.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class DateUtils {
    private DateUtils() {
    }

    public static List<LocalDate> getMonthsBetween(LocalDateTime start, LocalDateTime end) {
        LocalDate startDate = start.toLocalDate().withDayOfMonth(1);
        LocalDate endDate = end.toLocalDate().withDayOfMonth(1);

        List<LocalDate> dates = new ArrayList<>();

        do {
            dates.add(startDate);
            startDate = startDate.plusMonths(1);
        } while (!startDate.isAfter(endDate));

        return dates;
    }
}
