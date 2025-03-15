package com.ryanair.task2.domain.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ryanair.task2.datasource.RouteDataSource;
import com.ryanair.task2.domain.mappers.Mappers;
import com.ryanair.task2.domain.model.RouteGraphNode;
import com.ryanair.task2.domain.services.RouteService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class RouteServiceImpl implements RouteService {
    private static final String CACHE_KEY = "airports";

    private final RouteDataSource routeDataSource;

    private final Cache<String, Map<String, RouteGraphNode>> ryanairRouteGraphs = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(1)
            .build();

    public RouteServiceImpl(RouteDataSource routeDataSource) {
        this.routeDataSource = routeDataSource;
    }

    private Mono<Map<String, RouteGraphNode>> loadRouteGraph(String validOperator) {
        final int LOCALE = 3;

        return routeDataSource.getRoutes(LOCALE)
                .filter(route -> Objects.isNull(route.connectingAirport()) && route.operator().toUpperCase().trim().equals(validOperator))
                .collectList()
                .map(Mappers::routeApiDTOsToRouteGraph);
    }


    @Override
    public Mono<Map<String, RouteGraphNode>> getRouteGraphs(String validOperator) {
        return Mono.defer(() -> {
            Map<String, RouteGraphNode> cachedAirports = ryanairRouteGraphs.getIfPresent(CACHE_KEY);
            if (cachedAirports != null) {
                return Mono.just(cachedAirports);
            } else {
                return loadRouteGraph(validOperator)
                        .doOnSuccess(airports -> ryanairRouteGraphs.put(CACHE_KEY, airports));
            }
        });
    }

    @Override
    public Flux<List<String>> getItineraries(String departure, String arrival, int stops, String validOperator) {
        return getRouteGraphs(validOperator)
                .flatMapMany(airports -> Flux.fromIterable(airports.get(departure).getItineraries(arrival, stops)));
    }

}
