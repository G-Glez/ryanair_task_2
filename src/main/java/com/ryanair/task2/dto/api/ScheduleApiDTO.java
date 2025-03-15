package com.ryanair.task2.dto.api;

import java.time.LocalTime;

public record ScheduleApiDTO(
        int month,
        DayDTO[] days) {
    public record DayDTO(
            int day,
            FlightDTO[] flights) {
        public record FlightDTO(
                String carrierCode,
                String number,
                LocalTime departureTime,
                LocalTime arrivalTime) {
        }
    }
}

