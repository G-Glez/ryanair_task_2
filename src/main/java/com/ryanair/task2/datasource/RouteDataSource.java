package com.ryanair.task2.datasource;

import com.ryanair.task2.dto.api.RouteApiDTO;
import reactor.core.publisher.Flux;

public interface RouteDataSource {
    Flux<RouteApiDTO> getRoutes(int locale);
}
