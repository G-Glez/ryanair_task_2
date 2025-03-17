package com.ryanair.task2.dto.api;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

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
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                FlightDTO flightDTO = (FlightDTO) o;
                return Objects.equals(number, flightDTO.number) && Objects.equals(carrierCode, flightDTO.carrierCode) && Objects.equals(arrivalTime, flightDTO.arrivalTime) && Objects.equals(departureTime, flightDTO.departureTime);
            }

            @Override
            public int hashCode() {
                return Objects.hash(carrierCode, number, departureTime, arrivalTime);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DayDTO dayDTO = (DayDTO) o;
            return day == dayDTO.day && Objects.deepEquals(flights, dayDTO.flights);
        }

        @Override
        public int hashCode() {
            return Objects.hash(day, Arrays.hashCode(flights));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleApiDTO that = (ScheduleApiDTO) o;
        return month == that.month && Objects.deepEquals(days, that.days);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, Arrays.hashCode(days));
    }
}

