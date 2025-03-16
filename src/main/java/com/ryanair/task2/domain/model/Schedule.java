package com.ryanair.task2.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"departureTime", "arrivalTime"})

public final class Schedule {
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
}
