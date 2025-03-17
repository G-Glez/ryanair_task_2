package com.ryanair.task2.datasource.impl;

import com.ryanair.task2.datasource.RouteDataSource;
import com.ryanair.task2.datasource.exceptions.RemoteErrorException;
import com.ryanair.task2.dto.api.RouteApiDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class RouteDataSourceImpl implements RouteDataSource {
    static final String REQUEST_ERROR_MESSAGE = "Error: request to external service (Ryanair SCHEDULES) failed";

    private final WebClient webClient;

    public RouteDataSourceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .build();
    }

    @Override
    public Flux<RouteApiDTO> getRoutes(int locale) {
        final String PATH = "/views/locate/{locale}/routes";

        return webClient
                .get()
                .uri(PATH, locale)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new RemoteErrorException(REQUEST_ERROR_MESSAGE, HttpStatus.SERVICE_UNAVAILABLE)))
                .bodyToFlux(RouteApiDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(RemoteErrorException.class::isInstance)
                        .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> retrySignal.failure())));
    }
}
