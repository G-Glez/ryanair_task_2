package com.ryanair.task2.datasource.impl;

import com.ryanair.task2.datasource.ScheduleDataSource;
import com.ryanair.task2.dto.api.ScheduleApiDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ScheduleDataSourceImpl implements ScheduleDataSource {
    static final String BASE_URL = "https://services-api.ryanair.com";

    private final WebClient webClient;

    public ScheduleDataSourceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(BASE_URL)
                .build();
    }

    @Override
    public Mono<ScheduleApiDTO> getSchedules(int timetable, String departure, String arrival, int year, int month) {
        final String ENDPOINT = "/timtbl/{timetable}/schedules/{departure}/{arrival}/years/{year}/months/{month}";

        return webClient
                .get()
                .uri(ENDPOINT, timetable, departure, arrival, year, month)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ScheduleApiDTO.class)
                .retry(3);
    }
}
