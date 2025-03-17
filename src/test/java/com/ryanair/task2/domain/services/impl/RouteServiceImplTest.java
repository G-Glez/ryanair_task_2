package com.ryanair.task2.domain.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ryanair.task2.datasource.RouteDataSource;
import com.ryanair.task2.domain.model.RouteGraphNode;
import com.ryanair.task2.dto.api.RouteApiDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {
    @Mock
    private RouteDataSource routeDataSource;

    private Cache<String, Map<String, RouteGraphNode>> routeGraphCache;

    private RouteServiceImpl routeServiceImpl;

    @BeforeEach
    void setUp() {
        routeGraphCache = Caffeine.newBuilder()
                .build();
        routeServiceImpl = new RouteServiceImpl(routeDataSource, routeGraphCache);
    }

    @DisplayName("Test for RouteServiceImpl.checkIsValidRoute")
    @Test
    void testCheckIsValidRoute() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method checkIsValidRouteMethod = RouteServiceImpl.class.getDeclaredMethod("checkIsValidRoute", RouteApiDTO.class, String.class);

        List<RouteApiDTO> validRoutes = List.of(
                new RouteApiDTO("A", "B", null, true, true, "O1", ""),
                new RouteApiDTO("A", "C", null, false, true, "O1", "some"),
                new RouteApiDTO("A", "D", null, true, false, "O1", ""),
                new RouteApiDTO("A", "E", null, false, false, "O1", "some"));

        List<RouteApiDTO> notValidRoutes = List.of(
                new RouteApiDTO(null, "B", null, true, true, "O1", ""),
                new RouteApiDTO("A", null, null, false, true, "O1", "some"),
                new RouteApiDTO("A", "D", "", true, false, "O1", ""),
                new RouteApiDTO("A", "E", null, false, false, "O2", "some"));

        checkIsValidRouteMethod.setAccessible(true);

        for (RouteApiDTO route : validRoutes) {
            assertTrue((boolean) checkIsValidRouteMethod.invoke(routeServiceImpl, route, "O1"));
        }

        for (RouteApiDTO route : notValidRoutes) {
            assertFalse((boolean) checkIsValidRouteMethod.invoke(routeServiceImpl, route, "O1"));
        }
    }

    @DisplayName("Test for RouteServiceImpl.loadRouteGraph")
    @Test
    void testLoadRouteGraph() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        when(routeDataSource.getRoutes(anyInt())).thenReturn(routeDataSourceGetRoutesResponse());

        Method loadRouteGraphMethod = RouteServiceImpl.class.getDeclaredMethod("loadRouteGraph", String.class);

        loadRouteGraphMethod.setAccessible(true);

        Mono<Map<String, RouteGraphNode>> result = (Mono<Map<String, RouteGraphNode>>) loadRouteGraphMethod.invoke(routeServiceImpl, "O1");

        result.subscribe(routeGraph -> {
            assertTrue(routeGraph.containsKey("A"));
            assertTrue(routeGraph.containsKey("B"));
            assertTrue(routeGraph.containsKey("C"));
            assertTrue(routeGraph.containsKey("D"));
            assertTrue(routeGraph.containsKey("E"));
            assertTrue(routeGraph.containsKey("F"));
            assertTrue(routeGraph.get("A").getConnectedNodes().contains(routeGraph.get("B")));
            assertTrue(routeGraph.get("A").getConnectedNodes().contains(routeGraph.get("C")));
            assertTrue(routeGraph.get("A").getConnectedNodes().contains(routeGraph.get("D")));
            assertTrue(routeGraph.get("A").getConnectedNodes().contains(routeGraph.get("E")));
            assertTrue(routeGraph.get("B").getConnectedNodes().contains(routeGraph.get("A")));
            assertTrue(routeGraph.get("C").getConnectedNodes().contains(routeGraph.get("A")));
            assertTrue(routeGraph.get("C").getConnectedNodes().contains(routeGraph.get("E")));
            assertTrue(routeGraph.get("D").getConnectedNodes().contains(routeGraph.get("A")));
            assertTrue(routeGraph.get("D").getConnectedNodes().contains(routeGraph.get("E")));
            assertTrue(routeGraph.get("E").getConnectedNodes().contains(routeGraph.get("A")));
            assertTrue(routeGraph.get("E").getConnectedNodes().contains(routeGraph.get("C")));
            assertTrue(routeGraph.get("E").getConnectedNodes().contains(routeGraph.get("D")));
            assertTrue(routeGraph.get("E").getConnectedNodes().contains(routeGraph.get("F")));
        });
    }

    @DisplayName("Test for RouteServiceImpl.getRouteGraphs, verifying cache")
    @Test
    void testGetRouteGraphsCache() {
        when(routeDataSource.getRoutes(anyInt())).thenReturn(routeDataSourceGetRoutesResponse());

        assertEquals(0, routeGraphCache.estimatedSize());

        routeServiceImpl.getRouteGraphs("O1").block();

        assertNotNull(routeGraphCache.getIfPresent("O1"));

        routeGraphCache.put("O1", createRouteGraph());

        assertSame(routeServiceImpl.getRouteGraphs("O1").block(), routeGraphCache.getIfPresent("O1"));
    }

    @DisplayName("Test for RouteServiceImpl.getRouteGraphs, verifying cache does not cache errors")
    @Test
    void testGetRouteGraphsCacheError() {
        when(routeDataSource.getRoutes(anyInt())).thenReturn(Flux.error(new RuntimeException()));

        assertEquals(0, routeGraphCache.estimatedSize());

        routeServiceImpl.getRouteGraphs("O1").onErrorResume(e -> Mono.empty()).block();

        assertEquals(0, routeGraphCache.estimatedSize());
    }

    @DisplayName("Test for RouteServiceImpl.getItineraries")
    @Test
    void testGetItineraries() {
        when(routeDataSource.getRoutes(anyInt())).thenReturn(routeDataSourceGetRoutesResponse());

        Flux<List<String>> itineraries = routeServiceImpl.getItineraries("A", "F", 1, "O1");


        StepVerifier.FirstStep<List<String>> verifier = StepVerifier.create(itineraries);

        createRouteGraph().get("A").getItineraries("F", 1)
                .forEach(verifier::expectNext);

        verifier.verifyComplete();
    }

    private static Map<String, RouteGraphNode> createRouteGraph() {
        RouteGraphNode nodeA = new RouteGraphNode("A");
        RouteGraphNode nodeB = new RouteGraphNode("B");
        RouteGraphNode nodeC = new RouteGraphNode("C");
        RouteGraphNode nodeD = new RouteGraphNode("D");
        RouteGraphNode nodeE = new RouteGraphNode("E");
        RouteGraphNode nodeF = new RouteGraphNode("F");

        nodeA.getConnectedNodes().addAll(List.of(nodeB, nodeC, nodeD, nodeE));
        nodeB.getConnectedNodes().add(nodeA);
        nodeC.getConnectedNodes().addAll(List.of(nodeA, nodeE));
        nodeD.getConnectedNodes().addAll(List.of(nodeA, nodeE));
        nodeE.getConnectedNodes().addAll(List.of(nodeA, nodeC, nodeD, nodeF));
        return Map.of(
                "A", nodeA,
                "B", nodeB,
                "C", nodeC,
                "D", nodeD,
                "E", nodeE,
                "F", nodeF);
    }

    private static Flux<RouteApiDTO> routeDataSourceGetRoutesResponse() {
        return Flux.fromIterable(
                List.of(new RouteApiDTO("A", "B", null, true, true, "O1", ""),
                        new RouteApiDTO("A", "C", null, true, true, "O1", ""),
                        new RouteApiDTO("A", "D", null, true, true, "O1", ""),
                        new RouteApiDTO("A", "E", null, true, true, "O1", ""),
                        new RouteApiDTO("B", "A", null, true, true, "O1", ""),
                        new RouteApiDTO("C", "A", null, true, true, "O1", ""),
                        new RouteApiDTO("C", "E", null, true, true, "O1", ""),
                        new RouteApiDTO("D", "A", null, true, true, "O1", ""),
                        new RouteApiDTO("D", "E", null, true, true, "O1", ""),
                        new RouteApiDTO("E", "A", null, true, true, "O1", ""),
                        new RouteApiDTO("E", "C", null, true, true, "O1", ""),
                        new RouteApiDTO("E", "D", null, true, true, "O1", ""),
                        new RouteApiDTO("E", "F", null, true, true, "O1", ""))
        );
    }
}
