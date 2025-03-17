package com.ryanair.task2.domain.services.impl;

import com.ryanair.task2.domain.model.Schedule;
import com.ryanair.task2.domain.services.RouteService;
import com.ryanair.task2.domain.services.ScheduleService;
import com.ryanair.task2.dto.output.InterconnectionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {
    static final LocalDateTime UTIL_DATE_TIME = LocalDateTime.of(2021, 1, 1, 0, 0);

    @Mock
    private RouteService routeService;

    @Mock
    private ScheduleService scheduleService;

    private FlightServiceImpl flightServiceImpl;

    @BeforeEach
    void setUp() {
        flightServiceImpl = new FlightServiceImpl(routeService, scheduleService);
    }

    @DisplayName("Test for FlightServiceImpl.getInterconnections")
    @Test
    void testGetInterconnections() {
        when(routeService.getItineraries(anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(Flux.fromIterable(createItinerary()));

        when(scheduleService.getSchedulesFromItinerary(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), anyInt()))
                .thenReturn(Flux.fromIterable(createSchedules()));

        List<InterconnectionDTO> expectedResult = List.of(
                new InterconnectionDTO(1, new InterconnectionDTO.LegDTO[]{
                        new InterconnectionDTO.LegDTO("A", "B", UTIL_DATE_TIME, UTIL_DATE_TIME),
                        new InterconnectionDTO.LegDTO("B", "C", UTIL_DATE_TIME, UTIL_DATE_TIME)
                }),
                new InterconnectionDTO(1, new InterconnectionDTO.LegDTO[]{
                        new InterconnectionDTO.LegDTO("A", "B", UTIL_DATE_TIME, UTIL_DATE_TIME),
                        new InterconnectionDTO.LegDTO("B", "C", UTIL_DATE_TIME, UTIL_DATE_TIME)
                })
        );

        List<InterconnectionDTO> result = flightServiceImpl.getInterconnections("DUB", "STN", UTIL_DATE_TIME, UTIL_DATE_TIME).collectList().block();

        assertEquals(expectedResult, result);
    }

    private static List<List<String>> createItinerary() {
        return List.of(List.of("A", "B", "C"));
    }

    private static List<List<Schedule>> createSchedules() {
        return List.of(
                List.of(
                        new Schedule(UTIL_DATE_TIME, UTIL_DATE_TIME),
                        new Schedule(UTIL_DATE_TIME, UTIL_DATE_TIME)),
                List.of(
                        new Schedule(UTIL_DATE_TIME, UTIL_DATE_TIME),
                        new Schedule(UTIL_DATE_TIME, UTIL_DATE_TIME))
        );
    }
}