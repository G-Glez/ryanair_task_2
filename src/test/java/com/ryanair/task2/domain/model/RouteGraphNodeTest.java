package com.ryanair.task2.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteGraphNodeTest {

    private RouteGraphNode nodeA;
    private RouteGraphNode nodeB;
    private RouteGraphNode nodeC;
    private RouteGraphNode nodeD;
    private RouteGraphNode nodeE;
    private RouteGraphNode nodeF;

    @BeforeEach
    void setUp() {
        nodeA = new RouteGraphNode("A");
        nodeB = new RouteGraphNode("B");
        nodeC = new RouteGraphNode("C");
        nodeD = new RouteGraphNode("D");
        nodeE = new RouteGraphNode("E");
        nodeF = new RouteGraphNode("F");

        nodeA.getConnectedNodes().addAll(List.of(nodeB, nodeC, nodeD, nodeE));
        nodeB.getConnectedNodes().add(nodeA);
        nodeC.getConnectedNodes().addAll(List.of(nodeA, nodeE));
        nodeD.getConnectedNodes().addAll(List.of(nodeA, nodeE));
        nodeE.getConnectedNodes().addAll(List.of(nodeA, nodeC, nodeD, nodeF));
    }

    @Test
    void testGetItinerariesDirectRoute() {
        List<List<String>> itineraries = nodeA.getItineraries("B", 0);
        assertEquals(1, itineraries.size());
        assertEquals(List.of("A", "B"), itineraries.getFirst());
    }

    @Test
    void testGetItinerariesToSameNode() {
        List<List<String>> itinerariesWithNoStops = nodeA.getItineraries("A", 0);
        assertEquals(Collections.emptyList(), itinerariesWithNoStops);

        List<List<String>> itinerariesWithOneStop = nodeA.getItineraries("A", 2);
        assertEquals(Collections.emptyList(), itinerariesWithOneStop);
    }

    @Test
    void testGetItinerariesWithOneStop() {
        List<List<String>> itineraries = nodeA.getItineraries("E", 1);

        List<List<String>> expectedItineraries = List.of(
                List.of("A", "E"),
                List.of("A", "C", "E"),
                List.of("A", "D", "E"));

        assertEquals(3, itineraries.size());

        for (List<String> expectedItinerary : expectedItineraries) {
            assertTrue(itineraries.contains(expectedItinerary));
        }
    }

    @Test
    void testGetItinerariesWithStrops() {
        List<List<String>> itineraries = nodeB.getItineraries("F", 5);

        List<List<String>> expectedItineraries = List.of(
                List.of("B", "A", "E", "F"),
                List.of("B", "A", "C", "E", "F"),
                List.of("B", "A", "D", "E", "F"));

        assertEquals(3, itineraries.size());

        for (List<String> expectedItinerary : expectedItineraries) {
            assertTrue(itineraries.contains(expectedItinerary));
        }
    }

    @Test
    void testGetItinerariesNoRoute() {
        List<List<String>> itineraries = nodeA.getItineraries("F", 0);
        assertTrue(itineraries.isEmpty());
    }
}