package com.ryanair.task2.datasource.impl;

import com.ryanair.task2.datasource.RouteDataSource;
import com.ryanair.task2.dto.api.RouteApiDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class RouteDataSourceImpl implements RouteDataSource {
    private final WebClient webClient;

    public RouteDataSourceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Flux<RouteApiDTO> getRoutes(int locale) {
        return webClient
                .get()
                .uri("https://services-api.ryanair.com/views/locate/{locale}/routes", locale)
                .retrieve()
                .bodyToFlux(RouteApiDTO.class);
    }
}
