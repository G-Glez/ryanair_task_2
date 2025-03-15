package com.ryanair.task2.domain.services;

import com.ryanair.task2.domain.model.Schedule;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    Flux<List<Schedule>> getSchedulesFromItinerary(List<String> itinerary, LocalDateTime departureTime, LocalDateTime arrivalTime, int transferTime);
}
