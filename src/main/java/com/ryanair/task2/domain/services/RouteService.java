package com.ryanair.task2.domain.services;

import com.ryanair.task2.domain.model.RouteGraphNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface RouteService {
    Mono<Map<String, RouteGraphNode>> getRouteGraphs(String validOperator);

    Flux<List<String>> getItineraries(String departure, String arrival, int stops, String validOperator);
}
