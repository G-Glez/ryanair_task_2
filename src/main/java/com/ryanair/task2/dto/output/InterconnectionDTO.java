package com.ryanair.task2.dto.output;

import java.time.LocalDateTime;

public record InterconnectionDTO(
        int stops,
        LegDTO[] legs) {
    public record LegDTO(
            String departureAirport,
            String arrivalAirport,
            LocalDateTime departureDateTime,
            LocalDateTime arrivalDateTime) {
    }
}
