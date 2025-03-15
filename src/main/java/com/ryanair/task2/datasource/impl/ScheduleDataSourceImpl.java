package com.ryanair.task2.datasource.impl;

import com.ryanair.task2.datasource.ScheduleDataSource;
import com.ryanair.task2.dto.api.ScheduleApiDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ScheduleDataSourceImpl implements ScheduleDataSource {
    private final WebClient webClient;

    public ScheduleDataSourceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<ScheduleApiDTO> getSchedules(int timetable, String departure, String arrival, int year, int month) {
        final String URL = "https://services-api.ryanair.com/timtbl/{timetable}/schedules/{departure}/{arrival}/years/{year}/months/{month}";

        return webClient
                .get()
                .uri(URL, timetable, departure, arrival, year, month)
                .retrieve()
                .bodyToMono(ScheduleApiDTO.class);
    }
}
