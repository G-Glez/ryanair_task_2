# Ryanair Task 2: Interconnecting Flights

This project is a solution to the Ryanair Task 2: Interconnecting Flights, second task of the Ryanair recruitment
process.
The project is a RESTful API that allows the user to find all the possible interconnecting flights between two airports.
The project is built in Java SpringBoot.

## Libraries

The project uses the following libraries:

- SpringBoot as the main framework
- Spring Webflux as web framework
- Caffeine as cache library
- Mockito + JUnit for testing purposes

## How this implementation works

The project build the response consuming the Ryanair API, and then filtering the results to get the interconnecting flights.

- **RouteService**: `Routes` endpoint is consumed to get all the routes available, and them are parsed to a Graph structure, where nodes are the airports and edges are the routes between them. This graph is used to find the possible itineraries between the two airports. This graph is the object that is cached.

- **ScheduleService**: `Schedule` endpoint is consumed to get the flights available for each route. This is done for each itinerary found between the two airports. It calls for every possible schedule for each route, and then filters the results to get the possible schedules for the interconnecting flights.

- **FlightService**: This service is the one that orchestrates the other two services. It gets the possible itineraries from the `RouteService`, and then gets the schedules from the `ScheduleService`. Then it combines the results to get the possible interconnecting flights.

## Improvements

`Main` branch is the version completed up to the deadline date. There are some improvements that could be done:

- Add test for controller layer
- Add logs in critical points of the code (in this case, the external API calls)
- Add a better error handling mechanism for errors. In this use case, errors surely comes from the external API availability.
- Maybe add a cache for schedule endpoint. This is not implemented because the calls for that resource can differ a lot, but in some cases it could be useful.
