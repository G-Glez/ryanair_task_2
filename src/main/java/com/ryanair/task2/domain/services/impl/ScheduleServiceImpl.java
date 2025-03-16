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
        return Flux.fromIterable(ScheduleServiceUtils.getItinerarySteps(itinerary))
                .flatMap(step -> potentialSchedulesByStep(step, departureTime, arrivalTime).collectList())
                .buffer()
                .flatMap(schedulesBySteps -> Flux.fromStream(ScheduleServiceUtils.computeLegSchedules(schedulesBySteps).stream()
                        .filter(schedules -> ScheduleServiceUtils.checkValidItineraryTransferTime(schedules, transferTime))));
    }

    private Flux<Schedule> potentialSchedulesByStep(List<String> step, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        final int TIMETABLE = 3;

        List<LocalDate> monthsToRequest = DateUtils.getMonthsBetween(departureTime, arrivalTime);

        return Flux.fromIterable(monthsToRequest)
                .flatMap(date -> scheduleDataSource.getSchedules(TIMETABLE, step.getFirst(), step.getLast(), date.getYear(), date.getMonthValue())
                        .flatMapMany(dto -> Flux.fromIterable(Mappers.scheduleApiDTOToSchedule(dto, date.getYear()))))
                .filter(schedule -> ScheduleServiceUtils.checkValidDepartureAndArrivalTime(schedule, departureTime, arrivalTime));
    }
}

class ScheduleServiceUtils {
    public static boolean checkValidItineraryTransferTime(List<Schedule> schedules, int transferTime) {
        return IntStream.range(0, schedules.size() - 1)
                .allMatch(i -> schedules.get(i).getArrivalTime().plusHours(transferTime).isBefore(schedules.get(i + 1).getDepartureTime()));
    }

    public static boolean checkValidDepartureAndArrivalTime(Schedule schedules, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        return schedules.getDepartureTime().isAfter(departureTime) && schedules.getArrivalTime().isBefore(arrivalTime);
    }

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

    public static List<List<String>> getItinerarySteps(List<String> itinerary) {
        return IntStream
                .range(0, itinerary.size() - 1)
                .mapToObj(i -> Stream.of(itinerary.get(i), itinerary.get(i + 1)).toList())
                .toList();
    }

    private ScheduleServiceUtils() {
    }
}