package com.ryanair.task2.domain.services.impl;

import com.ryanair.task2.domain.model.Schedule;
import com.ryanair.task2.domain.services.FlightService;
import com.ryanair.task2.domain.services.RouteService;
import com.ryanair.task2.domain.services.ScheduleService;
import com.ryanair.task2.dto.output.InterconnectionDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class FlightServiceImpl implements FlightService {
    private final RouteService routeService;
    private final ScheduleService scheduleService;

    public FlightServiceImpl(RouteService routeService, ScheduleService scheduleService) {
        this.routeService = routeService;
        this.scheduleService = scheduleService;
    }

    @Override
    public Flux<InterconnectionDTO> getInterconnections(String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        final String VALID_OPERATOR = "RYANAIR";
        final int MAX_STOPS = 1;
        final int TRANSFER_TIME = 2;

        return routeService.getItineraries(departure, arrival, MAX_STOPS, VALID_OPERATOR)
                .flatMap(itinerary -> scheduleService.getSchedulesFromItinerary(itinerary, departureDateTime, arrivalDateTime, TRANSFER_TIME)
                        .map(schedules -> {
                            InterconnectionDTO.LegDTO[] legs = getLegs(itinerary, schedules);
                            return new InterconnectionDTO(itinerary.size() - 1, legs);
                        }))
                .collectList()
                .flatMapMany(Flux::fromIterable);
    }

    private InterconnectionDTO.LegDTO[] getLegs(List<String> itinerary, List<Schedule> schedules) {
        return IntStream.range(0, itinerary.size() - 1).mapToObj(
                i -> new InterconnectionDTO.LegDTO(
                        itinerary.get(i),
                        itinerary.get(i + 1),
                        schedules.get(i).getDepartureTime(),
                        schedules.get(i).getArrivalTime())
        ).toArray(InterconnectionDTO.LegDTO[]::new);
    }
}
