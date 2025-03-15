package com.ryanair.task2.domain.services;

import com.ryanair.task2.dto.output.InterconnectionDTO;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface FlightService {
    Flux<InterconnectionDTO> getInterconnections(String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);
}
