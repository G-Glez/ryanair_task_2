package com.ryanair.task2.domain.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class RouteGraphNode {
    private final String airportIATACode;
    private final Set<RouteGraphNode> connectedNodes;

    public RouteGraphNode(String airportIATACode) {
        this.airportIATACode = airportIATACode;
        this.connectedNodes = new HashSet<>();
    }

    public List<List<String>> getItineraries(String arrivalAirportIATACode, int stops) {

        List<List<String>> itineraries = new ArrayList<>();
        List<String> currentItinerary = new ArrayList<>();
        Set<RouteGraphNode> visitedNode = new HashSet<>();

        currentItinerary.add(this.airportIATACode);
        dfs(this, arrivalAirportIATACode, stops + 2, currentItinerary, itineraries, visitedNode);

        return itineraries;
    }

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
