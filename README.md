# Smart Campus API

This is a JAX-RS RESTful service for managing Rooms and Sensors in a Smart Campus environment. It does not use external databases but rather relies on static in-memory data structures to simulate persistence during runtime.

## Project Architecture
- **Framework**: JAX-RS (Jersey Implementation)
- **Server**: Grizzly HTTP Server (Standalone execution without Tomcat/Glassfish)
- **Data Store**: Thread-safe in-memory collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`)
- **Exception Handling**: Custom exception classes and `ExceptionMapper`s map domain errors to appropriate HTTP status codes (e.g., 403, 409, 422, 500).
- **Logging**: A custom JAX-RS `ContainerRequestFilter` and `ContainerResponseFilter` using `java.util.logging` to observe and trace requests and responses.

## Build and Launch Instructions

1. Ensure you have Java 11+ and Maven installed.
2. Open a terminal and navigate to this project's root directory (`smart-campus-api`).
3. Compile and build the project using Maven:
   ```bash
   mvn clean package
   ```
4. Run the generated fat-jar to start the standalone Grizzly server:
   ```bash
   java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
   ```
   *The server will start listening on `http://localhost:8080/`.*

## Sample `curl` Commands

Here are 5 commands demonstrating successful interactions with the API:

1. **Discovery Endpoint**
   ```bash
   curl -i -X GET http://localhost:8080/api/v1
   ```
2. **List all Rooms**
   ```bash
   curl -i -X GET http://localhost:8080/api/v1/rooms
   ```
3. **Get specific Room metadata**
   ```bash
   curl -i -X GET http://localhost:8080/api/v1/rooms/LIB-301
   ```
4. **Filter Sensors by Type**
   ```bash
   curl -i -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"
   ```
5. **Get Readings for a Sensor**
   ```bash
   curl -i -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings
   ```

## Report Questions & Answers

**Q1: Default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures.**
By default, JAX-RS resource classes are request-scoped, meaning a new instance is created for each incoming request. To manage in-memory data (like Maps/Lists) and prevent data loss or race conditions across concurrent requests, the data structures must be static (class-level) or handled by a singleton data store class using thread-safe collections like `ConcurrentHashMap` or `CopyOnWriteArrayList`.

**Q2: Why is the provision of "Hypermedia" (HATEOAS) considered a hallmark of advanced RESTful design? How does this approach benefit client developers compared to static documentation?**
HATEOAS (Hypermedia as the Engine of Application State) allows clients to dynamically navigate the API through provided links rather than hardcoding URIs. This decouples the client from the API's URL structure, making the API more resilient to backend routing changes and allowing it to be self-discoverable, reducing the client's reliance on external static documentation.

**Q3: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.**
Returning full objects increases network bandwidth usage and client processing overhead, especially for large datasets. Returning only IDs reduces payload size and bandwidth, but requires clients to make additional requests (N+1 problem) to get details if needed. The choice depends on the specific use case's performance vs. data requirements.

**Q4: Is the DELETE operation idempotent in your implementation? Provide a detailed justification.**
Yes, the DELETE operation is idempotent. If a client mistakenly sends the exact same DELETE request for a room multiple times, the first request will delete the room and return a 204 No Content. Subsequent identical DELETE requests will also have the same outcome (the room remains deleted) and should return a 404 Not Found (or 204), meaning the server's final state is exactly the same as after the first deletion.

**Q5: We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain.**
If a client sends data in a format not listed in `@Consumes` (e.g., text/plain), JAX-RS will reject the request with an HTTP 415 Unsupported Media Type error. This happens because the framework cannot find an appropriate `MessageBodyReader` to unmarshal the incoming payload into the expected Java type.

**Q6: Contrast filtering using @QueryParam with an alternative design where the type is part of the URL path. Why is the query parameter approach generally considered superior for filtering?**
Query parameters (`?type=CO2`) are superior for filtering because they represent optional constraints on a collection. A URL path (`/type/CO2`) implies a hierarchical resource structure and strict endpoints, making it less flexible and much harder to maintain when combining multiple optional filters (e.g., `?type=CO2&status=ACTIVE`).

**Q7: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity?**
The Sub-Resource Locator pattern delegates the handling of nested paths to separate resource classes, promoting modularity and single responsibility. It prevents massive "god" controller classes, making the codebase easier to maintain, test, and read, especially as the API grows in complexity and the depth of nested resources increases.

**Q8: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?**
HTTP 422 Unprocessable Entity is more accurate than 404 Not Found here because the endpoint itself (`/sensors`) exists and the JSON syntax is valid, but the semantic content (the specific `roomId` inside the payload) is invalid or cannot be processed due to a business rule constraint. 404 should be reserved for when the actual target URI is not found.

**Q9: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers.**
Exposing internal stack traces reveals sensitive information about the application's underlying technology stack, internal class names, package structures, and potentially versions of libraries being used. An attacker can use this footprinting information to identify known vulnerabilities in those specific versions or libraries, aiding in targeted exploitation.

**Q10: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?**
JAX-RS filters provide a centralized, aspect-oriented approach for cross-cutting concerns like logging. This keeps the core business logic in resource methods clean and focused, ensures consistent logging across all endpoints without code duplication, and makes the logging mechanism easier to maintain, test, or modify globally from a single place.
