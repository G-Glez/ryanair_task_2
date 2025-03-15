package com.ryanair.task2.datasource;

import com.ryanair.task2.dto.api.ScheduleApiDTO;
import reactor.core.publisher.Mono;

public interface ScheduleDataSource {
    Mono<ScheduleApiDTO> getSchedules(int timetable, String departure, String arrival, int year, int month);
}
