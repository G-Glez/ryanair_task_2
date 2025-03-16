package com.ryanair.task2.domain.services.impl;


import com.ryanair.task2.datasource.ScheduleDataSource;
import com.ryanair.task2.domain.model.Schedule;
import com.ryanair.task2.dto.api.ScheduleApiDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {
    @Mock
    private ScheduleDataSource scheduleDataSource;

    private ScheduleServiceImpl scheduleServiceImpl;

    @BeforeEach
    void setUp() {
        scheduleServiceImpl = new ScheduleServiceImpl(scheduleDataSource);
    }

    @DisplayName("Test for ScheduleServiceImpl.getSchedulesFromItinerary")
    @Test
    void testGetSchedulesFromItinerary() {
        when(scheduleDataSource.getSchedules(3, "A", "B", 2025, 1))
                .thenReturn(Mono.just(createScheduleApiDTO()));

        when(scheduleDataSource.getSchedules(3, "B", "C", 2025, 1))
                .thenReturn(Mono.just(createScheduleApiDTO()));

        List<List<Schedule>> expectedResult = List.of(
                List.of(
                        new Schedule(LocalDateTime.of(2025, 1, 1, 10, 0), LocalDateTime.of(2025, 1, 1, 13, 0)),
                        new Schedule(LocalDateTime.of(2025, 1, 1, 16, 0), LocalDateTime.of(2025, 1, 1, 19, 0))
                )
        );

        List<List<Schedule>> result = scheduleServiceImpl.getSchedulesFromItinerary(List.of("A", "B", "C"), LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 1, 2, 10, 0), 2)
                .collectList()
                .block();


        assertEquals(expectedResult, result);
    }

    private static ScheduleApiDTO createScheduleApiDTO() {
        return new ScheduleApiDTO(
                1,
                new ScheduleApiDTO.DayDTO[]{
                        new ScheduleApiDTO.DayDTO(
                                1,
                                new ScheduleApiDTO.DayDTO.FlightDTO[]{
                                        new ScheduleApiDTO.DayDTO.FlightDTO("", "", LocalTime.of(10, 0), LocalTime.of(13, 0)),
                                        new ScheduleApiDTO.DayDTO.FlightDTO("", "", LocalTime.of(16, 0), LocalTime.of(19, 0))
                                }

                        ),
                });

    }

    @DisplayName("Test for ScheduleServiceUtils.checkValidItineraryTransferTime")
    @Test
    void testCheckValidTransferTime() {
        List<Schedule> validItinerarySchedules = List.of(
                new Schedule(LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 1, 1, 0, 0)),
                new Schedule(LocalDateTime.of(2025, 1, 1, 2, 1), LocalDateTime.of(2025, 1, 1, 2, 1)),
                new Schedule(LocalDateTime.of(2025, 1, 2, 2, 2), LocalDateTime.of(2025, 1, 2, 2, 2)),
                new Schedule(LocalDateTime.of(2025, 3, 2, 2, 3), LocalDateTime.of(2025, 3, 2, 2, 3)),
                new Schedule(LocalDateTime.of(2026, 3, 2, 2, 4), LocalDateTime.of(2026, 3, 2, 2, 4))
        );

        List<Schedule> invalidItinerarySchedules = List.of(
                new Schedule(LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 1, 1, 0, 0)),
                new Schedule(LocalDateTime.of(2025, 1, 1, 1, 59), LocalDateTime.of(2025, 1, 1, 1, 59))
        );

        assertTrue(ScheduleServiceImpl.checkValidItineraryTransferTime(validItinerarySchedules, 2));
        assertFalse(ScheduleServiceImpl.checkValidItineraryTransferTime(invalidItinerarySchedules, 2));
    }

    @DisplayName("Test for ScheduleServiceUtils.checkValidDepartureAndArrivalTime")
    @Test
    void testCheckValidDepartureAndArrivalTime() {
        LocalDateTime departureTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime arrivalTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Schedule validSchedule = new Schedule(departureTime.plus(Duration.ofHours(2)), arrivalTime.minus(Duration.ofHours(2)));
        List<Schedule> invalidSchedules = List.of(
                new Schedule(departureTime.minus(Duration.ofHours(2)), arrivalTime.minus(Duration.ofHours(2))),
                new Schedule(departureTime.plus(Duration.ofHours(2)), arrivalTime.plus(Duration.ofHours(2))),
                new Schedule(departureTime.minus(Duration.ofHours(2)), arrivalTime.plus(Duration.ofHours(2)))
        );

        assertTrue(ScheduleServiceImpl.checkValidDepartureAndArrivalTime(validSchedule, departureTime, arrivalTime));
        invalidSchedules.forEach(schedule -> assertFalse(ScheduleServiceImpl.checkValidDepartureAndArrivalTime(schedule, departureTime, arrivalTime)));
    }

    @DisplayName("Test for ScheduleServiceUtils.computeLegSchedules")
    @Test
    void testComputeLegSchedules() {
        Schedule schedule1 = new Schedule(LocalDateTime.now(), LocalDateTime.now());
        Schedule schedule2 = new Schedule(LocalDateTime.now(), LocalDateTime.now());
        Schedule schedule3 = new Schedule(LocalDateTime.now(), LocalDateTime.now());
        Schedule schedule4 = new Schedule(LocalDateTime.now(), LocalDateTime.now());


        List<List<Schedule>> potentialSchedules = List.of(
                List.of(
                        schedule1,
                        schedule2
                ),
                List.of(
                        schedule3,
                        schedule4
                )
        );

        List<List<Schedule>> expectedResult = List.of(
                List.of(
                        schedule1,
                        schedule3),
                List.of(
                        schedule1,
                        schedule4),
                List.of(
                        schedule2,
                        schedule3),
                List.of(
                        schedule2,
                        schedule4)
        );

        List<List<Schedule>> result = ScheduleServiceImpl.computeLegSchedules(potentialSchedules);

        assertEquals(expectedResult, result);
    }

    @DisplayName("Test for ScheduleServiceUtils.getItinerarySteps")
    @Test
    void testGetItinerarySteps() {
        List<String> itinerary = List.of("A", "B", "C", "D");

        List<List<String>> expectedSteps = List.of(
                List.of("A", "B"),
                List.of("B", "C"),
                List.of("C", "D")
        );

        List<List<String>> result = ScheduleServiceImpl.getItinerarySteps(itinerary);

        assertEquals(expectedSteps, result);
    }
}
