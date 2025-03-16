package com.ryanair.task2.datasource;

import com.ryanair.task2.dto.api.ScheduleApiDTO;
import reactor.core.publisher.Mono;

/**
 * Data source for schedules
 */
public interface ScheduleDataSource {
    /**
     * Get schedules
     *
     * @param timetable timetable
     * @param departure departure
     * @param arrival   arrival
     * @param year      year
     * @param month     month
     * @return          ScheduleApiDTO as a mono
     */
    Mono<ScheduleApiDTO> getSchedules(int timetable, String departure, String arrival, int year, int month);
}
