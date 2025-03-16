package com.ryanair.task2.domain.mappers;

import com.ryanair.task2.domain.model.RouteGraphNode;
import com.ryanair.task2.domain.model.Schedule;
import com.ryanair.task2.dto.api.RouteApiDTO;
import com.ryanair.task2.dto.api.ScheduleApiDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MappersTests {
    @DisplayName("Test for Mappers.routeApiDTOsToRouteGraph")
    @Test
    void testRouteApiDTOsToRouteGraph() {
        List<RouteApiDTO> routes = createRouteDTOs();

        Map<String, RouteGraphNode> graph = Mappers.routeApiDTOsToRouteGraph(routes);

        assertEquals(4, graph.size());
        assertTrue(graph.containsKey("A"));
        assertTrue(graph.containsKey("B"));
        assertTrue(graph.containsKey("C"));
        assertTrue(graph.containsKey("D"));
        assertTrue(graph.get("A").getConnectedNodes().contains(graph.get("B")));
        assertTrue(graph.get("A").getConnectedNodes().contains(graph.get("D")));
        assertTrue(graph.get("B").getConnectedNodes().contains(graph.get("A")));
        assertTrue(graph.get("B").getConnectedNodes().contains(graph.get("C")));
        assertTrue(graph.get("C").getConnectedNodes().contains(graph.get("D")));
        assertTrue(graph.get("D").getConnectedNodes().isEmpty());
    }

    private static List<RouteApiDTO> createRouteDTOs() {
        RouteApiDTO routeAB = new RouteApiDTO("A", "B", null, false, false, null, null);
        RouteApiDTO routeAD = new RouteApiDTO("A", "D", null, false, false, null, null);
        RouteApiDTO routeBA = new RouteApiDTO("B", "A", null, false, false, null, null);
        RouteApiDTO routeBC = new RouteApiDTO("B", "C", null, false, false, null, null);
        RouteApiDTO routeCD = new RouteApiDTO("C", "D", null, false, false, null, null);

        return List.of(routeAB, routeAD, routeBA, routeBC, routeCD);
    }

    @DisplayName("Test for Mappers.routeApiDTOsToRouteGraph with an empty list of routes as input")
    @Test
    void testRouteApiDTOsToRouteGraphWithEmptyRoutes() {
        Map<String, RouteGraphNode> graph = Mappers.routeApiDTOsToRouteGraph(List.of());

        assertTrue(graph.isEmpty());
    }

    @DisplayName("Test for Mappers.scheduleApiDTOToSchedule")
    @Test
    void testScheduleApiDTOToSchedule() {
        ScheduleApiDTO scheduleApiDTO = generateScheduleDTO();

        List<Schedule> schedules = Mappers.scheduleApiDTOToSchedule(scheduleApiDTO, 2023);

        assertEquals(3, schedules.size());
        assertEquals(
                LocalDateTime.of(LocalDate.of(2023, 5, 1), LocalTime.of(10, 0)),
                schedules.get(0).getDepartureTime());
        assertEquals(
                LocalDateTime.of(LocalDate.of(2023, 5, 1), LocalTime.of(12, 0)),
                schedules.get(0).getArrivalTime());
        assertEquals(
                LocalDateTime.of(LocalDate.of(2023, 5, 2), LocalTime.of(14, 0)),
                schedules.get(1).getDepartureTime());
        assertEquals(
                LocalDateTime.of(LocalDate.of(2023, 5, 2), LocalTime.of(16, 0)),
                schedules.get(1).getArrivalTime());
        assertEquals(
                LocalDateTime.of(LocalDate.of(2023, 5, 2), LocalTime.of(15, 0)),
                schedules.get(2).getDepartureTime());
        assertEquals(
                LocalDateTime.of(LocalDate.of(2023, 5, 2), LocalTime.of(18, 0)),
                schedules.get(2).getArrivalTime());
    }

    private static ScheduleApiDTO generateScheduleDTO() {
        ScheduleApiDTO.DayDTO.FlightDTO flight1 = new ScheduleApiDTO.DayDTO.FlightDTO(null, null, LocalTime.of(10, 0), LocalTime.of(12, 0));
        ScheduleApiDTO.DayDTO.FlightDTO flight2 = new ScheduleApiDTO.DayDTO.FlightDTO(null, null, LocalTime.of(14, 0), LocalTime.of(16, 0));
        ScheduleApiDTO.DayDTO.FlightDTO flight3 = new ScheduleApiDTO.DayDTO.FlightDTO(null, null, LocalTime.of(15, 0), LocalTime.of(18, 0));
        ScheduleApiDTO.DayDTO day1 = new ScheduleApiDTO.DayDTO(1, new ScheduleApiDTO.DayDTO.FlightDTO[]{flight1});
        ScheduleApiDTO.DayDTO day2 = new ScheduleApiDTO.DayDTO(2, new ScheduleApiDTO.DayDTO.FlightDTO[]{flight2, flight3});
        return new ScheduleApiDTO(5, new ScheduleApiDTO.DayDTO[]{day1, day2});
    }

    @DisplayName("Test for Mappers.scheduleApiDTOToSchedule with an empty list of schedules as input")
    @Test
    void testScheduleApiDTOToScheduleWithEmptyFlights() {
        ScheduleApiDTO scheduleApiDTO = new ScheduleApiDTO(5, new ScheduleApiDTO.DayDTO[0]);

        List<Schedule> schedules = Mappers.scheduleApiDTOToSchedule(scheduleApiDTO, 2023);

        assertTrue(schedules.isEmpty());
    }
}
