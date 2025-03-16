package com.ryanair.task2.dto.output;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;


public record InterconnectionDTO(
        int stops,
        LegDTO[] legs) {
    public record LegDTO(
            String departureAirport,
            String arrivalAirport,
            LocalDateTime departureDateTime,
            LocalDateTime arrivalDateTime) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LegDTO legDTO = (LegDTO) o;

            return departureAirport.equals(legDTO.departureAirport) &&
                    arrivalAirport.equals(legDTO.arrivalAirport) &&
                    departureDateTime.equals(legDTO.departureDateTime) &&
                    arrivalDateTime.equals(legDTO.arrivalDateTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterconnectionDTO that = (InterconnectionDTO) o;

        if (stops != that.stops) return false;

        return Arrays.equals(legs, that.legs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stops) + Arrays.hashCode(legs);
    }
}
