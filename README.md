# Account Service CQRS & Event Sourcing

> Maintained by **Soufiane Kousta** – Cybersecurity student engineer- TP .

## Project Overview
This repository contains a primary **Account Service** plus a companion **Analytics Service** that demonstrates how to build a CQRS (Command Query Responsibility Segregation) + Event Sourcing system on the JVM:

- **Command side** exposes REST endpoints to create accounts and emit credit/debit commands through Axon Framework. Domain invariants are enforced inside an aggregate that applies domain events instead of mutating state directly @src/main/java/com/kousta/accountservicecqrses/commands/aggregates/AccountAggregate.java#17-84.
- **Query/Analytics side** listens to updates through Axon subscription queries and streams real-time analytics via Reactor `Flux` endpoints (server-sent events) @analytics-service/src/main/java/com/kousta/analyticsservice/controllers/AccountAnalyticsController.java#19-44.

The stack highlights:
- Spring Boot 3.5, Java 17, Maven
- Axon Framework 4.10 for commands/events/subscription queries
- MySQL (command side) and in-memory H2 (analytics side)
- Reactor + WebFlux for live analytics streaming

## Repository Structure
```
account-service-cqrs-es/
├── pom.xml                     # Parent Maven project
├── src/main/java/...           # Account service (commands & queries)
├── analytics-service/          # Independent Spring Boot module for analytics
└── README.md                   # You are here
```

### Account Service (port 8083)
- `commands/controllers/AccountCommandController` exposes `/commands/account` REST endpoints that send Axon commands through `CommandGateway` @src/main/java/com/kousta/accountservicecqrses/commands/controllers/AccountCommandController.java#18-60.
- `commands/aggregates/AccountAggregate` contains the aggregate root that validates intent (no negative balances) and applies `AccountCreated`, `AccountCredited`, and `AccountDebited` events @src/main/java/com/kousta/accountservicecqrses/commands/aggregates/AccountAggregate.java#28-84.
- Default datasource points to MySQL (create DB if missing) and uses Axon Jackson serializers @src/main/resources/application.properties#1-10.

### Analytics Service (port 8084)
- Depends on the parent module for shared commands/events and exposes query endpoints via `QueryGateway`.
- `AccountAnalyticsController` offers:
  - `/query/accountAnalytics` – fetch all analytics rows.
  - `/query/accountAnalytics/{accountId}` – fetch analytics for one account.
  - `/query/accountAnalytics/{accountId}/watch` – **Server-Sent Events** stream using Reactor `Flux` for live updates @analytics-service/src/main/java/com/kousta/analyticsservice/controllers/AccountAnalyticsController.java#26-43.
- Uses in-memory H2; WebFlux dependency is already configured in `analytics-service/pom.xml`.

## Prerequisites
1. **Java 17** (`java -version`).
2. **Maven 3.9+** (`mvn -version`).
3. **MySQL** accessible at `localhost:3306` with credentials configured via env vars or `application.properties`.
4. Optional: Axon Server if you want full-blown event routing (not required for local tests).

## Running the Services
1. **Start databases**
   ```bash
   docker run --name mysql-ebank -e MYSQL_ROOT_PASSWORD=secret -p 3306:3306 -d mysql:8
   ```
2. **Build everything** from the repo root:
   ```bash
   mvn clean install
   ```
3. **Run the Account Service** (port 8083):
   ```bash
   mvn spring-boot:run
   ```
4. In another terminal, **run the Analytics Service** (port 8084):
   ```bash
   cd analytics-service
   mvn spring-boot:run
   ```

> Tip: Import the Maven root into IntelliJ/VS Code and launch both apps from the IDE.

## API Quickstart
### Command endpoints (Account Service)
| Method | Path | Body | Description |
| ------ | ---- | ---- | ----------- |
| `POST` | `/commands/account/create` | `{ "currency": "MAD", "initialBalance": 1000 }` | Emits `CreateAccountCommand`.
| `POST` | `/commands/account/credit` | `{ "accountId": "...", "amount": 200, "currency": "MAD" }` | Emits `CreditAccountCommand`.
| `POST` | `/commands/account/debit` | `{ "accountId": "...", "amount": 50, "currency": "MAD" }` | Emits `DebitAccountCommand`.

### Query/Analytics endpoints (Analytics Service)
| Method | Path | Description |
| ------ | ---- | ----------- |
| `GET` | `/query/accountAnalytics` | Returns all analytics entries.
| `GET` | `/query/accountAnalytics/{accountId}` | Returns analytics for a single account.
| `GET` | `/query/accountAnalytics/{accountId}/watch` | Server-Sent Events stream (keep-alive connection) delivering initial state plus real-time updates.

Use tools like `curl`, Postman, or a browser tab (for SSE) to interact. When commands emit events, Axon propagates them so analytics projections stay in sync.

## Configuration Summary
- **Account Service**: `application.properties` configures MySQL URL, username, password, Hibernate dialect, and Axon serializers @src/main/resources/application.properties#1-10.
- **Analytics Service**: in-memory H2 plus Axon serializers defined in `analytics-service/src/main/resources/application.properties` @analytics-service/src/main/resources/application.properties#1-7.

## Development Notes
- Keep aggregates slim; push read-model calculations to the analytics module.
- When adding new event types, remember to update both the aggregate (command side) and analytics projections/subscription queries.
- Tests can leverage Axon test fixtures or Spring Boot slice tests (starters already included in both modules).

## About the Author
**Soufiane Kousta** – TP CQRS-ES; CCN3