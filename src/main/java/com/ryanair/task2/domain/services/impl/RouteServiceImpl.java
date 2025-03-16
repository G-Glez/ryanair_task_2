package com.ryanair.task2.domain.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.ryanair.task2.datasource.RouteDataSource;
import com.ryanair.task2.domain.mappers.Mappers;
import com.ryanair.task2.domain.model.RouteGraphNode;
import com.ryanair.task2.domain.services.RouteService;
import com.ryanair.task2.dto.api.RouteApiDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteDataSource routeDataSource;

    private final Cache<String, Map<String, RouteGraphNode>> ryanairRouteGraphs;

    public RouteServiceImpl(RouteDataSource routeDataSource, Cache<String, Map<String, RouteGraphNode>> ryanairRouteGraphs) {
        this.routeDataSource = routeDataSource;
        this.ryanairRouteGraphs = ryanairRouteGraphs;
    }

    private Mono<Map<String, RouteGraphNode>> loadRouteGraph(String validOperator) {
        final int LOCALE = 3;

        return routeDataSource.getRoutes(LOCALE)
                .filter(route -> checkIsValidRoute(route, validOperator))
                .collectList()
                .map(Mappers::routeApiDTOsToRouteGraph);
    }

    private boolean checkIsValidRoute(RouteApiDTO route, String validOperator) {
        return Objects.isNull(route.connectingAirport()) &&
                route.operator().toUpperCase().trim().equals(validOperator) &&
                Objects.nonNull(route.airportFrom()) &&
                Objects.nonNull(route.airportTo());
    }


    @Override
    public Mono<Map<String, RouteGraphNode>> getRouteGraphs(String validOperator) {
        return Mono.defer(() -> {
            Map<String, RouteGraphNode> cachedAirports = ryanairRouteGraphs.getIfPresent(validOperator);
            if (cachedAirports != null) {
                return Mono.just(cachedAirports);
            } else {
                return loadRouteGraph(validOperator)
                        .doOnSuccess(airports -> ryanairRouteGraphs.put(validOperator, airports));
            }
        });
    }

    @Override
    public Flux<List<String>> getItineraries(String departure, String arrival, int stops, String validOperator) {
        return getRouteGraphs(validOperator)
                .flatMapMany(airports -> Flux.fromIterable(airports.get(departure).getItineraries(arrival, stops)));
    }

}
