package com.ryanair.task2.domain.model;

import lombok.Getter;

import java.util.*;

@Getter
public class RouteGraphNode {
    private final String airportIATACode;
    private final Set<RouteGraphNode> connectedNodes;

    public RouteGraphNode(String airportIATACode) {
        this.airportIATACode = airportIATACode;
        this.connectedNodes = new HashSet<>();
    }

    /**
     * Get all possible itineraries from this airport to the arrival airport with a maximum number of stops
     *
     * @param arrivalAirportIATACode The arrival airport IATA code
     * @param maxStops               The maximum number of stops
     * @return A list of itineraries
     */
    public List<List<String>> getItineraries(String arrivalAirportIATACode, int maxStops) {
        if (airportIATACode.equals(arrivalAirportIATACode)) {
            return Collections.emptyList();
        }

        List<List<String>> itineraries = new ArrayList<>();
        List<String> currentItinerary = new ArrayList<>();
        Set<RouteGraphNode> visitedNode = new HashSet<>();

        currentItinerary.add(this.airportIATACode);
        dfs(this, arrivalAirportIATACode, maxStops + 2, currentItinerary, itineraries, visitedNode);

        return itineraries;
    }

    /**
     * Depth-first search to find all possible itineraries from the current airport to the arrival airport
     *
     * @param current        The current airport
     * @param arrival        The arrival airport
     * @param remainingStops The remaining number of stops
     * @param currentPath    The current itinerary
     * @param paths          The list of itineraries
     * @param visited        The set of visited airports
     */
    private void dfs(RouteGraphNode current, String arrival, int remainingStops,
                     List<String> currentPath, List<List<String>> paths, Set<RouteGraphNode> visited) {
        if (remainingStops == 0) return;

        if (current.getAirportIATACode().equals(arrival)) {
            paths.add(new ArrayList<>(currentPath));
            return;
        }

        visited.add(current);

        for (RouteGraphNode neighbor : current.connectedNodes) {
            if (!visited.contains(neighbor)) {
                currentPath.add(neighbor.getAirportIATACode());
                dfs(neighbor, arrival, remainingStops - 1, currentPath, paths, visited);
                currentPath.removeLast();
            }
        }

        visited.remove(current);
    }
}
