package com.ryanair.task2.datasource.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanair.task2.datasource.exceptions.RemoteErrorException;
import com.ryanair.task2.dto.api.ScheduleApiDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

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

    @DisplayName("Check retries with 3 failed requests and 1 successful request")
    @Test
    void checkValidRetry() throws JsonProcessingException {
        // Server response
        ScheduleApiDTO mockServerResponse = new ScheduleApiDTO(2, new ScheduleApiDTO.DayDTO[]{});

        // 3 server failed responses before the successful response
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        // Successful server response
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockServerResponse))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        Mono<ScheduleApiDTO> response = scheduleDataSource.getSchedules(0, "", "", 0, 0);

        // Expect the successful response
        StepVerifier.create(response)
                .expectNext(mockServerResponse)
                .verifyComplete();
    }

    @DisplayName("Check retries with 4 failed requests")
    @Test
    void checkInvalidRetry() {
        // 4 server failed responses
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        Mono<ScheduleApiDTO> response = scheduleDataSource.getSchedules(0, "", "", 0, 0);

        // Expect an error
        StepVerifier.create(response)
                .expectError(RemoteErrorException.class)
                .verify();
    }
}