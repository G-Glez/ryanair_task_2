package com.ryanair.task2.datasource.impl;

import com.ryanair.task2.datasource.ScheduleDataSource;
import com.ryanair.task2.dto.api.ScheduleApiDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class ScheduleDataSourceImpl implements ScheduleDataSource {
    private final WebClient webClient;

    public ScheduleDataSourceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Flux<ScheduleApiDTO> getSchedules(int timetable, String departure, String arrival, int year, int month) {
        return webClient
                .get()
                .uri("https://services-api.ryanair.com/timtbl/{timtbl}/schedules/{departure}/{arrival}/years/{year}/months/{month}", timetable, departure, arrival, year, month)
                .retrieve()
                .bodyToFlux(ScheduleApiDTO.class);
    }
}
