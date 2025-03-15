package com.ryanair.task2.dto.api;

public record RouteApiDTO(
        String airportFrom,
        String airportTo,
        String connectingAirport,
        boolean newRoute,
        boolean seasonalRoute,
        String operator,
        String group) {
}
