package com.ryanair.task2.domain.services;

import com.ryanair.task2.domain.model.RouteGraphNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface RouteService {
    /**
     * Get route graphs for a valid operator
     * @param  validOperator valid flight operator for the route
     * @return map of route graphs, key is the airport code
     */
    Mono<Map<String, RouteGraphNode>> getRouteGraphs(String validOperator);

    /**
     * Get possible itineraries between two airports
     * @param  departure departure airport
     * @param  arrival arrival airport
     * @param  maxStops maximum stops
     * @param  validOperator valid flight operator
     * @return list of itineraries, each itinerary as a list of strings with the airport name
     */
    Flux<List<String>> getItineraries(String departure, String arrival, int maxStops, String validOperator);
}
