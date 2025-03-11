package com.ryanair.task2.api.controllers;

import com.ryanair.task2.dto.output.InterconnectionDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/interconnections")
public class InterconnectionController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<InterconnectionDTO> getInterconnections(
            @RequestParam("departure")
            String departure,
            @RequestParam("arrival")
            String arrival,
            @RequestParam("departureDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime departureDateTime,
            @RequestParam("arrivalDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime arrivalDateTime) {
        return null;
    }
}
