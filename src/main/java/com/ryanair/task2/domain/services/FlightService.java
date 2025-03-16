package com.ryanair.task2.domain.services;

import com.ryanair.task2.dto.output.InterconnectionDTO;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface FlightService {
    /**
     * Get interconnections between two airports
     * @param  departure departure airport
     * @param  arrival arrival airport
     * @param  departureDateTime departure date and time
     * @param  arrivalDateTime arrival date and time
     * @return flux of interconnections
     */
    Flux<InterconnectionDTO> getInterconnections(String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);
}
