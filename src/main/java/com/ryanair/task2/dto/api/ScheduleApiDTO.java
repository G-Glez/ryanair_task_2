package com.ryanair.task2.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleApiDTO {
    private int month;

    private DayDTO[] days;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class DayDTO {
        private int day;
        private FlightDTO[] flights;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        static class FlightDTO {
            private String number;
            private LocalTime departureTime;
            private LocalTime arrivalTime;
        }
    }
}

