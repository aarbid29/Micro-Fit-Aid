# Micro-Fit-Aid

A fitness tracking application built on a microservices architecture using Spring Boot. Users can log workout activities and automatically receive AI-generated fitness recommendations powered by Google Gemini. The system uses Keycloak for authentication, Apache Kafka for async communication between services, and Spring Cloud for service discovery and centralized configuration.

---
#Architecture
<img width="1024" height="559" alt="IMG_4241" src="https://github.com/user-attachments/assets/134716c4-c9c2-4596-b553-e3c51404fe02" />






---



## Services

### API Gateway
**Port:** 8080

The single entry point for all external requests. Built with Spring Cloud Gateway (WebFlux / reactive).

- Validates JWT tokens by fetching Keycloak's public keys from the `jwk-set-uri` (`http://localhost:8181/realms/fit-aid/protocol/openid-connect/certs`). Validation is purely cryptographic — no credentials are checked here.
- Runs a `KeycloakUserSyncFilter` on every authenticated request. This filter parses the JWT using `nimbus-jose-jwt`, extracts the user's Keycloak ID, email, and name, then calls the User Service to register the user in the local database if they do not already exist.
- Injects an `X-User-ID` header (set to the Keycloak `sub` claim) into every forwarded request so downstream services can identify the caller without handling JWTs themselves.
- Routes requests by path prefix using Eureka-based load balancing (`lb://SERVICENAME`), meaning no hardcoded service addresses are needed.
- CORS is configured to allow requests from `http://localhost:5173`.

---

### User Service (`userproject`)
**Port:** 8089
**Database:** PostgreSQL (`microuser` database)

Manages user profiles in the application's own database, independent of Keycloak's identity store.

Endpoints:
- `POST /api/users/register` — creates a new user record. Called automatically by the Gateway's sync filter on a user's first login.
- `GET /api/users/{userId}` — retrieves a user's profile.
- `GET /api/users/{userId}/validate` — returns true or false indicating whether a user with the given Keycloak ID exists. Used by the Activity Service before accepting a new activity log.

The `User` entity stores: `keycloakId`, `email`, `firstName`, `lastName`, `password` (BCrypt-hashed), `role`, and timestamps.

---

### Activity Service (`activityservice`)
**Port:** 8083
**Database:** MongoDB (`activity` database)
**Outbound:** Kafka producer, HTTP call to User Service

Handles the logging and retrieval of workout activities.

Endpoints:
- `POST /api/activities` — logs a new activity. Before persisting, calls `UserValidationService` which makes a synchronous HTTP GET to the User Service's `/validate` endpoint to confirm the user exists. After saving, publishes the full activity object as JSON to the `activity-events` Kafka topic.
- `GET /api/activities` — returns all activities belonging to the authenticated user, identified via the `X-User-ID` header.

The `Activity` document includes: `userId`, `type` (enum: RUNNING, CYCLING, SWIMMING, WEIGHTLIFTING, YOGA, WALKING, HIIT), `duration`, `caloriesBurned`, `startTime`, and `additionalMetrics` (a flexible `Map<String, Object>` for extra data).

---

### AI Service (`aiservice`)
**Port:** 8085
**Database:** MongoDB (`aiuse` database)
**Inbound:** Kafka consumer
**Outbound:** HTTP to Google Gemini API

Generates personalized fitness recommendations by analyzing logged activities using Google Gemini.

- `ActivityMessageListener` is a `@KafkaListener` subscribed to the `activity-events` topic with consumer group `activity-processor-group`. Every time an activity is published by the Activity Service, this listener receives it automatically and triggers the recommendation pipeline.
- `ActivityAIService` builds a structured prompt from the activity data and sends it to the Gemini REST API. The prompt instructs Gemini to return a JSON object containing: overall analysis, pace analysis, heart rate analysis, calorie analysis, improvement areas, next workout suggestions, and safety guidelines.
- The JSON response is parsed and persisted as a `Recommendation` document in MongoDB, linked to both the `activityId` and `userId`.

Endpoints:
- `GET /api/recommendations/user/{userId}` — all recommendations for a user.
- `GET /api/recommendations/activity/{activityId}` — the recommendation generated for a specific activity.

---

### Config Server (`configserver`)
**Port:** 8087

A Spring Cloud Config Server running in `native` mode. It serves YAML configuration files from its own classpath (`/resources/config/`) to all other services at startup. This is where database URLs, Kafka broker addresses, Eureka URLs, Gemini API credentials, and server ports are centrally defined.

Each service connects to the Config Server via:
```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8087
```

Config files served:
- `gateway.yml` — gateway port, Keycloak `jwk-set-uri`, routing rules
- `userproject.yml` — PostgreSQL connection, JPA settings
- `activityservice.yml` — MongoDB URI, Kafka producer config, port
- `aiservice.yml` — MongoDB URI, Kafka consumer config, Gemini API URL and key, port

---

### Eureka Server (`eureka`)
**Port:** 8761

A Netflix Eureka service registry. Every microservice registers itself here on startup with its name and network address. The Gateway (and any service making inter-service calls) queries Eureka to resolve service locations dynamically using the `lb://` scheme. This means services can be restarted or scaled without updating any configuration elsewhere.

---

## Authentication Flow

Keycloak is configured with realm `fit-aid` and runs on port 8181.

1. The user authenticates via the frontend. Keycloak issues a signed JWT access token containing the claims `sub` (unique Keycloak user ID), `email`, `given_name`, and `family_name`.
2. The frontend sends this token as `Authorization: Bearer <token>` with every API request.
3. The Gateway validates the token's signature using Keycloak's public keys fetched from the `jwk-set-uri`. No request reaches a downstream service without a valid token.
4. The `KeycloakUserSyncFilter` parses the validated token, extracts user details, and calls the User Service to auto-register the user if it is their first login. This keeps the local database in sync with Keycloak without requiring a separate registration step from the client.
5. The `X-User-ID` header is injected into the forwarded request. Downstream services use this header for all user-scoped operations and never handle JWTs directly.

---

## Tech Stack

| Component          | Technology                                      |
|--------------------|-------------------------------------------------|
| Language           | Java 21                                         |
| Framework          | Spring Boot 4.0.1                               |
| API Gateway        | Spring Cloud Gateway (WebFlux)                  |
| Service Discovery  | Netflix Eureka (Spring Cloud 2025.1.0)          |
| Config Management  | Spring Cloud Config Server (native mode)        |
| Authentication     | Keycloak (OAuth2 / JWT via nimbus-jose-jwt)     |
| Messaging          | Apache Kafka                                    |
| AI                 | Google Gemini REST API                          |
| User Database      | PostgreSQL (Spring Data JPA / Hibernate)        |
| Activity Database  | MongoDB (Spring Data MongoDB)                   |
| Recommendations DB | MongoDB (Spring Data MongoDB)                   |
| Boilerplate        | Lombok                                          |

---

## Running the Project

### Prerequisites

- Java 21
- Maven
- A running Keycloak instance with realm `fit-aid` configured
- PostgreSQL with a database named `microuser`
- MongoDB
- Apache Kafka with Zookeeper
- Google Gemini API key

### Startup Order

Services must be started in this order because each depends on the ones before it:

1. **Config Server** — must be up first so all other services can fetch their configuration on startup
2. **Eureka Server** — must be running before services try to register themselves
3. **Keycloak** — must be running before the Gateway can validate tokens
4. **User Service**
5. **Activity Service**
6. **AI Service**
7. **API Gateway** — start last, once all downstream services are registered in Eureka

### Environment Variables

The following environment variables must be set for the AI Service and Activity Service (they are referenced as placeholders in the config files):

```
GEMINI_URL=<Google Gemini API endpoint>
GEMINI_KEY=<your Gemini API key>
```

### Building and Running a Service

From within any individual service directory:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

---

## Port Reference

| Service          | Port |
|------------------|------|
| API Gateway      | 8080 |
| User Service     | 8089 |
| Activity Service | 8083 |
| AI Service       | 8085 |
| Config Server    | 8087 |
| Eureka Server    | 8761 |
| Keycloak         | 8181 |

---

## Data Flow Summary

```
User logs in
  -> Keycloak issues JWT

User sends POST /api/activities with JWT
  -> Gateway validates JWT signature against Keycloak public keys
  -> KeycloakUserSyncFilter syncs user to PostgreSQL if first login
  -> Gateway injects X-User-ID header, routes to Activity Service

Activity Service receives request
  -> Calls User Service to validate user exists
  -> Saves activity document to MongoDB
  -> Publishes activity to Kafka topic (activity-events)

AI Service Kafka consumer receives activity event
  -> Builds structured prompt with activity data
  -> Calls Google Gemini API
  -> Parses JSON response
  -> Saves recommendation document to MongoDB

User sends GET /api/recommendations/user/{userId}
  -> Gateway routes to AI Service
  -> Returns stored recommendations
```
