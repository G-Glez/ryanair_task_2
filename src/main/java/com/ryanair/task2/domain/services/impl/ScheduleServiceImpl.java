package com.ryanair.task2.domain.services.impl;

import com.ryanair.task2.datasource.ScheduleDataSource;
import com.ryanair.task2.domain.mappers.Mappers;
import com.ryanair.task2.domain.model.Schedule;
import com.ryanair.task2.domain.services.ScheduleService;
import com.ryanair.task2.util.DateUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleDataSource scheduleDataSource;

    public ScheduleServiceImpl(ScheduleDataSource scheduleDataSource) {
        this.scheduleDataSource = scheduleDataSource;
    }

    @Override
    public Flux<List<Schedule>> getSchedulesFromItinerary(List<String> itinerary, LocalDateTime departureTime, LocalDateTime arrivalTime, int transferTime) {
        List<List<String>> steps = getItinerarySteps(itinerary);

        return Flux.fromIterable(steps)
                .flatMap(step -> potentialSchedulesByStep(step, departureTime, arrivalTime).collectList())
                .buffer()
                .flatMap(schedulesBySteps -> Flux.fromStream(computeLegSchedules(schedulesBySteps).stream()
                        .filter(schedules -> checkValidItineraryTransferTime(schedules, transferTime))));
    }

    /**
     * Get the potential schedules for a step in an itinerary
     *
     * @param step          list of airport codes in the step (2 elements)
     * @param departureTime departure time
     * @param arrivalTime   arrival time
     * @return flux of potential schedules
     */
    private Flux<Schedule> potentialSchedulesByStep(List<String> step, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        final int TIMETABLE = 3;
        // Maximum number of concurrent requests to the schedule data source
        final int MAX_CONCURRENT_REQUESTS = 5;

        List<LocalDate> monthsToRequest = DateUtils.getMonthsBetween(departureTime, arrivalTime);

        return Flux.fromIterable(monthsToRequest)
                .flatMap(date -> scheduleDataSource.getSchedules(TIMETABLE, step.getFirst(), step.getLast(), date.getYear(), date.getMonthValue())
                                .flatMapMany(dto -> Flux.fromIterable(Mappers.scheduleApiDTOToSchedule(dto, date.getYear()))),
                        MAX_CONCURRENT_REQUESTS)
                .filter(schedule -> checkValidDepartureAndArrivalTime(schedule, departureTime, arrivalTime));
    }

    /**
     * Check if the transfer time between flights in a list of schedules is valid
     *
     * @param schedules    list of schedules
     * @param transferTime transfer time between flights
     * @return true if the transfer time is valid, false otherwise
     */
    public static boolean checkValidItineraryTransferTime(List<Schedule> schedules, int transferTime) {
        return IntStream.range(0, schedules.size() - 1)
                .allMatch(i -> schedules.get(i).getArrivalTime().plusHours(transferTime).isBefore(schedules.get(i + 1).getDepartureTime()));
    }

    /**
     * Check if the departure and arrival time of a schedule are valid
     * Arrival time must be before the arrival time and departure time must be after the departure time
     *
     * @param schedules     schedule
     * @param departureTime departure time
     * @param arrivalTime   arrival time
     * @return true if the departure and arrival time are valid, false otherwise
     */
    public static boolean checkValidDepartureAndArrivalTime(Schedule schedules, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        return schedules.getDepartureTime().isAfter(departureTime) && schedules.getArrivalTime().isBefore(arrivalTime);
    }

    /**
     * Compute all the possible schedules for a step in an itinerary
     * This possible schedules are calculated as the cartesian product of the schedules of the step
     *
     * @param potentialSchedules list of schedules for an itinerary
     * @return list of possible schedules
     */
    public static List<List<Schedule>> computeLegSchedules(List<List<Schedule>> potentialSchedules) {
        if (potentialSchedules.isEmpty()) {
            return Collections.emptyList();
        }

        return computeLegSchedules(potentialSchedules, 0)
                .toList();
    }

    private static Stream<List<Schedule>> computeLegSchedules(List<List<Schedule>> potentialSchedules, int index) {
        if (index == potentialSchedules.size()) {
            return Stream.of(Collections.emptyList());
        }
        List<Schedule> currentSet = potentialSchedules.get(index);
        return currentSet.stream()
                .flatMap(element ->
                        computeLegSchedules(potentialSchedules, index + 1)
                                .map(list -> {
                                    List<Schedule> newList = new ArrayList<>(list);
                                    newList.addFirst(element);
                                    return newList;
                                })
                );
    }

    /**
     * Get the steps of an itinerary
     *
     * @param itinerary list of airport codes in the itinerary
     * @return list of steps
     */
    public static List<List<String>> getItinerarySteps(List<String> itinerary) {
        return IntStream
                .range(0, itinerary.size() - 1)
                .mapToObj(i -> Stream.of(itinerary.get(i), itinerary.get(i + 1)).toList())
                .toList();
    }
}
