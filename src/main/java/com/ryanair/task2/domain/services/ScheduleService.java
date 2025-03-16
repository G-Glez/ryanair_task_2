package com.ryanair.task2.domain.services;

import com.ryanair.task2.domain.model.Schedule;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    /**
     * Get schedules from an itinerary
     *
     * @param itinerary list of airport codes in the itinerary
     * @param departureTime departure time
     * @param arrivalTime arrival time
     * @param transferTime transfer time between flights
     * @return list of schedules
     */
    Flux<List<Schedule>> getSchedulesFromItinerary(List<String> itinerary, LocalDateTime departureTime, LocalDateTime arrivalTime, int transferTime);
}
