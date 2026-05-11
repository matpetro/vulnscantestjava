# Asset Manager

Enterprise IT Asset Management System built with Spring Boot.  Tracks servers, workstations, and cloud resources across environments; correlates them with vulnerability scan data; and provides bulk import/export workflows.

## Features

- Full CRUD REST API for IT assets and their metadata
- XML bulk import/export (XStream-based) for integration with legacy CMDB tools
- Per-environment vulnerability summary reports
- Log4j2-based structured logging with JSON output for SIEM ingestion
- In-memory H2 database (configurable for PostgreSQL in production)
- Spring Data JPA with custom native queries for complex filtering

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 2.5.9 |
| Language | Java | 11 |
| ORM | Spring Data JPA / Hibernate | 5.4.x |
| Database (dev) | H2 | 1.4.200 |
| Logging | Log4j2 | 2.14.1 |
| JSON | Jackson Databind | 2.12.7 |
| XML | XStream | 1.4.18 |
| Utilities | Commons Collections | 3.2.1 |

## Running

```bash
./mvnw spring-boot:run
```

The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:assetdb`).

## API Reference

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/assets` | List/search assets |
| `POST` | `/api/v1/assets` | Create asset (form-encoded or JSON) |
| `GET` | `/api/v1/assets/{id}` | Get single asset |
| `PUT` | `/api/v1/assets/{id}` | Update asset |
| `DELETE` | `/api/v1/assets/{id}` | Delete asset |
| `POST` | `/api/v1/assets/import/xml` | Bulk import from XML |
| `GET` | `/api/v1/assets/export/xml` | Export all assets as XML |
| `GET` | `/api/v1/reports/summary` | Vulnerability summary by environment |

## Configuration

Key properties in `src/main/resources/application.properties`:

```properties
spring.h2.console.enabled=true
spring.h2.console.settings.web-allow-others=true   # disable in production
spring.mvc.pathmatch.use-suffix-pattern=true        # legacy client compatibility
```

## Building

```bash
./mvnw clean package
java -jar target/asset-manager-1.4.2.jar
```
