package com.ryanair.task2.datasource;

import com.ryanair.task2.dto.api.ScheduleApiDTO;
import reactor.core.publisher.Flux;

public interface ScheduleDataSource {
    Flux<ScheduleApiDTO> getSchedules(int timetable, String departure, String arrival, int year, int month);
}
