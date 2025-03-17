package com.ryanair.task2.datasource.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanair.task2.dto.api.ScheduleApiDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleDataSourceImplTest {
    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Mock
    private WebClient.Builder webClientBuilder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ScheduleDataSourceImpl scheduleDataSource;

    @BeforeEach
    void setup() {
        WebClient webClient = WebClient.create(mockWebServer.url("/").toString());

        when(webClientBuilder.build()).thenReturn(webClient);

        scheduleDataSource = new ScheduleDataSourceImpl(webClientBuilder);
    }

    @Test
    void checkRetry() throws JsonProcessingException {
        ScheduleApiDTO mockServerResponse = new ScheduleApiDTO(2, new ScheduleApiDTO.DayDTO[]{});

        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockServerResponse))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        StepVerifier.create(scheduleDataSource.getSchedules(0, "", "", 0, 0))
                .expectNext(mockServerResponse)
                .verifyComplete();

        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockServerResponse))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        StepVerifier.create(scheduleDataSource.getSchedules(0, "", "", 0, 0))
                .expectError()
                .verify();
    }
}