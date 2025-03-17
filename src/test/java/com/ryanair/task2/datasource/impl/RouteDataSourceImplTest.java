package com.ryanair.task2.datasource.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanair.task2.datasource.exceptions.RemoteErrorException;
import com.ryanair.task2.dto.api.RouteApiDTO;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteDataSourceImplTest {

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

    private RouteDataSourceImpl routeDataSource;

    @BeforeEach
    void setup() {
        WebClient webClient = WebClient.create(mockWebServer.url("/").toString());

        when(webClientBuilder.build()).thenReturn(webClient);

        routeDataSource = new RouteDataSourceImpl(webClientBuilder);
    }


    @DisplayName("Check retries with 3 failed requests and 1 successful request")
    @Test
    void checkValidRetry() throws JsonProcessingException {
        // Server response
        List<RouteApiDTO> mockServerResponse = List.of(
                new RouteApiDTO("", "", "", false, false, "", "")
        );

        // 3 server failed responses before the successful response
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        // Successful server response
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockServerResponse))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        Flux<RouteApiDTO> response = routeDataSource.getRoutes(0);

        // Expect the successful response
        StepVerifier.create(response)
                .expectNext(mockServerResponse.getFirst())
                .verifyComplete();
    }

    @Test
    void checkInvalidRetry() {
        // 4 server failed responses
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        Flux<RouteApiDTO> response = routeDataSource.getRoutes(0);

        // Expect an error
        StepVerifier.create(response)
                .expectError(RemoteErrorException.class)
                .verify();
    }
}