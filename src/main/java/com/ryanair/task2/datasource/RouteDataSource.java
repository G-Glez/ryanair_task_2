package com.ryanair.task2.datasource;

import com.ryanair.task2.dto.api.RouteApiDTO;
import reactor.core.publisher.Flux;

/**
 * Data source for routes
 */
public interface RouteDataSource {
    /**
     * Get all routes
     *
     * @param locale locale
     * @return       all RouteApiDTO as a flux
     */
    Flux<RouteApiDTO> getRoutes(int locale);
}
