package com.ryanair.task2.datasource.impl;

import com.ryanair.task2.datasource.ScheduleDataSource;
import com.ryanair.task2.dto.api.ScheduleApiDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class ScheduleDataSourceImpl implements ScheduleDataSource {

    private final WebClient webClient;

    public ScheduleDataSourceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
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
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }
}
