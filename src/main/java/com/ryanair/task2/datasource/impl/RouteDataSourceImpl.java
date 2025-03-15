package com.ryanair.task2.datasource.impl;

import com.ryanair.task2.datasource.RouteDataSource;
import com.ryanair.task2.dto.api.RouteApiDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class RouteDataSourceImpl implements RouteDataSource {
    private final WebClient webClient;

    public RouteDataSourceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Flux<RouteApiDTO> getRoutes(int locale) {
        final String URL = "https://services-api.ryanair.com/views/locate/{locale}/routes";

        return webClient
                .get()
                .uri(URL, locale)
                .retrieve()
                .bodyToFlux(RouteApiDTO.class);
    }
}
