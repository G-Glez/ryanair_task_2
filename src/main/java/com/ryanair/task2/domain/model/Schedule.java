package com.ryanair.task2.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor

public final class Schedule {
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
}
