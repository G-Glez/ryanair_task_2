package com.ryanair.task2.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InterconnectionDTO {
    private int stops;

    private LegDTO[] legs;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class LegDTO {
        private String departureAirport;
        private String arrivalAirport;
        private LocalDateTime departureDateTime;
        private LocalDateTime arrivalDateTime;
    }
}
