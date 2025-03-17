package com.ryanair.task2.domain.mappers;

import com.ryanair.task2.domain.model.RouteGraphNode;
import com.ryanair.task2.domain.model.Schedule;
import com.ryanair.task2.dto.api.RouteApiDTO;
import com.ryanair.task2.dto.api.ScheduleApiDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class Mappers {
    /**
     * Map a list of RouteApiDTOs to a map of RouteGraphNodes
     *
     * @param routes list of RouteApiDTOs
     * @return map of RouteGraphNodes (route graph), key is the airport code
     */
    public static Map<String, RouteGraphNode> routeApiDTOsToRouteGraph(List<RouteApiDTO> routes) {
        return routes.stream()
                .collect(HashMap::new, (map, route) -> {
                    map.computeIfAbsent(route.airportFrom(), RouteGraphNode::new);
                    map.computeIfAbsent(route.airportTo(), RouteGraphNode::new);
                    map.get(route.airportFrom()).getConnectedNodes().add(map.get(route.airportTo()));
                }, HashMap::putAll);
    }

    /**
     * Map a ScheduleApiDTO to a list of Schedules
     *
     * @param scheduleApiDTO ScheduleApiDTO
     * @param year           year of the schedule
     * @return list of Schedules
     */
    public static List<Schedule> scheduleApiDTOToSchedule(ScheduleApiDTO scheduleApiDTO, int year) {
        return Stream.of(scheduleApiDTO.days())
                .flatMap(dayDTO -> Stream.of(dayDTO.flights())
                        .map(flightDTO -> new Schedule(
                                LocalDateTime.of(LocalDate.of(year, scheduleApiDTO.month(), dayDTO.day()), flightDTO.departureTime()),
                                LocalDateTime.of(LocalDate.of(year, scheduleApiDTO.month(), dayDTO.day()), flightDTO.arrivalTime())))
                ).toList();
    }

    private Mappers() {
    }
}
