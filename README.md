# 🚨 Incidents Management Platform

A production-ready, cloud-native **incident management system** built with a **Spring Cloud microservices architecture**. The platform enables teams to report, track, comment on, and resolve incidents in real time — with built-in authentication, file storage, and live notifications.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Infrastructure](#infrastructure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Running the Platform](#running-the-platform)
- [API Gateway Routes](#api-gateway-routes)
- [Security](#security)
- [Project Structure](#project-structure)
- [Contributing](#contributing)

---

## Overview

This platform provides a complete lifecycle for incident management:

- **Report** incidents with rich descriptions and file attachments
- **Comment** collaboratively on open incidents
- **Chat** in real time around specific incidents
- **Notify** team members of critical updates automatically
- **Manage users** with role-based access control via Keycloak

All services communicate through a central **API Gateway** and register themselves with **Eureka** for dynamic service discovery.

---

## Architecture

```
                        ┌──────────────────────────────────┐
                        │         API Gateway :8080         │
                        │   (Spring Cloud Gateway MVC)      │
                        │   OAuth2 / JWT validation         │
                        └────────────────┬─────────────────┘
                                         │
              ┌──────────────────────────┼──────────────────────────┐
              │                          │                           │
   ┌──────────▼──────┐      ┌────────────▼───────┐      ┌──────────▼──────┐
   │  User Service   │      │ Incident Service    │      │ Comment Service  │
   │   :8081         │      │                     │      │                  │
   └─────────────────┘      └─────────────────────┘      └─────────────────┘
              │                          │                           │
   ┌──────────▼──────┐      ┌────────────▼───────┐                  │
   │  Chat Service   │      │Notification Service │                  │
   │                 │      │                     │                  │
   └─────────────────┘      └─────────────────────┘                  │
              │                                                       │
              └───────────────────────────┬───────────────────────────┘
                                          │
              ┌───────────────────────────┼───────────────────────────┐
              │                           │                            │
   ┌──────────▼──────┐      ┌─────────────▼──────┐      ┌────────────▼──────┐
   │   PostgreSQL    │      │     Keycloak        │      │      MinIO        │
   │   (6 databases) │      │      :8180          │      │   :9000 / :9001   │
   └─────────────────┘      └────────────────────┘      └───────────────────┘
              │
   ┌──────────▼──────┐      ┌────────────────────┐
   │  Config Server  │      │   Eureka Server     │
   │    :8888        │      │     :8761           │
   └─────────────────┘      └────────────────────┘
```

All microservices:
- Pull their configuration from the **Config Server** at startup
- Register and discover each other via **Eureka**
- Are protected behind the **API Gateway**, which validates JWT tokens issued by Keycloak

---

## Services

| Service | Port | Description |
|---|---|---|
| **API Gateway** | `8080` | Single entry point — routes, load-balances, and enforces OAuth2/JWT auth |
| **Config Server** | `8888` | Centralized configuration from a Git repository |
| **Eureka Server** | `8761` | Service registry and discovery dashboard |
| **User Service** | `8081` | User management, profiles, avatar upload to MinIO |
| **Incident Service** | — | Create, update, assign, and resolve incidents |
| **Comment Service** | — | Threaded comments per incident |
| **Chat Service** | — | Real-time messaging around incidents |
| **Notification Service** | — | Push notifications on incident events |

---

## Tech Stack

### Backend
| Technology | Role |
|---|---|
| **Java 21 / Spring Boot 3** | Core framework for all microservices |
| **Spring Cloud Gateway (MVC)** | Reactive-free API Gateway with load balancing |
| **Spring Cloud Config** | Externalized, centralized configuration |
| **Netflix Eureka** | Service registry and client-side discovery |
| **Spring Security OAuth2** | JWT-based resource server on every service |
| **Spring Data JPA / Hibernate** | ORM and database access layer |
| **MinIO Java SDK** | Object storage for file uploads (avatars, attachments) |

### Infrastructure
| Technology | Role |
|---|---|
| **PostgreSQL 16** | Relational database — one schema per service |
| **Keycloak 23** | Identity and Access Management (IAM), OAuth2 / OpenID Connect |
| **MinIO** | S3-compatible object storage |
| **Docker / Docker Compose** | Containerization and local orchestration |

---

## Infrastructure

### Databases (PostgreSQL)

Each service owns its own database, enforcing strict data isolation:

| Database | Owner Service |
|---|---|
| `keycloak_db` | Keycloak |
| `user_db` | User Service |
| `incident_db` | Incident Service |
| `comment_db` | Comment Service |
| `notification_db` | Notification Service |
| `chat_db` | Chat Service |

### Keycloak

Keycloak is configured with a dedicated realm (`incidents-realm`) and handles:
- User authentication and token issuance
- Role-based authorization (RBAC)
- JWT signing and key management

The realm is imported automatically on first startup from `docker/keycloak/realm-export.json`.

### MinIO

MinIO provides S3-compatible object storage for file uploads:
- **API** — port `9000`
- **Console** — port `9001`
- User avatars are stored in the `avatars` bucket
- Pre-signed URLs are generated for secure, time-limited client-side access (expiry: 60 min)

---

## Getting Started

### Prerequisites

- [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/) v2+
- [Java 21](https://adoptium.net/) (for local service development)
- [Maven 3.9+](https://maven.apache.org/)

### Environment Variables

Copy the example file and adjust values before running:

```bash
cp .env.example .env
```

| Variable | Default | Description |
|---|---|---|
| `POSTGRES_APP_USER` | `incidents_app` | PostgreSQL application user |
| `POSTGRES_APP_PASSWORD` | `change-me-app-password` | PostgreSQL password — **change in production** |
| `POSTGRES_PORT` | `5432` | Exposed PostgreSQL port |
| `KEYCLOAK_ADMIN_USERNAME` | `admin` | Keycloak admin username |
| `KEYCLOAK_ADMIN_PASSWORD` | `admin` | Keycloak admin password — **change in production** |
| `KEYCLOAK_DB_NAME` | `keycloak_db` | Keycloak's database name |
| `MINIO_ROOT_USER` | `minioadmin` | MinIO root user |
| `MINIO_ROOT_PASSWORD` | `change-me-minio-password` | MinIO root password — **change in production** |
| `CONFIG_SERVER_PORT` | `8888` | Config Server port |
| `EUREKA_PORT` | `8761` | Eureka Server port |
| `GATEWAY_PORT` | `8080` | API Gateway port |
| `USER_SERVICE_PORT` | `8081` | User Service port |

### Running the Platform

**1. Build all service images**

```bash
mvn clean package -DskipTests
```

**2. Start the infrastructure and all services**

```bash
docker compose up --build
```

Services start in the correct dependency order:
1. PostgreSQL
2. Eureka Server
3. Config Server (depends on Eureka)
4. Keycloak (depends on PostgreSQL)
5. MinIO
6. All application microservices

**3. Verify everything is up**

| UI | URL |
|---|---|
| Eureka Dashboard | http://localhost:8761 |
| Config Server | http://localhost:8888/user-service/default |
| Keycloak Admin Console | http://localhost:8180 |
| MinIO Console | http://localhost:9001 |
| API Gateway | http://localhost:8080 |

---

## API Gateway Routes

All requests must go through the gateway at `http://localhost:8080`. Endpoints require a valid **Bearer token** (issued by Keycloak) except for `/actuator/health` and `/actuator/info`.

| Prefix | Routed To |
|---|---|
| `/api/users/**` | `USER-SERVICE` |
| `/api/incidents/**` | `INCIDENT-SERVICE` |
| `/api/comments/**` | `COMMENT-SERVICE` |
| `/api/chat/**` | `CHAT-SERVICE` |
| `/api/notifications/**` | `NOTIFICATION-SERVICE` |

**Example — obtain a token from Keycloak:**

```bash
curl -s -X POST \
  http://localhost:8180/realms/incidents-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=<client_id>&username=<user>&password=<pass>" \
  | jq .access_token
```

**Example — call a protected endpoint:**

```bash
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <your_token>"
```

---

## Security

Security is enforced at two levels:

### Gateway Level
The `SecurityConfig` validates every inbound JWT against Keycloak's public keys (via `jwk-set-uri`) before routing the request. No downstream service is reachable without a valid, non-expired token.

```
Client → [JWT validated by Gateway] → Microservice
```

### Service Level
Each microservice is also configured as an **OAuth2 Resource Server** and independently validates the JWT, providing defense in depth.

**Keycloak token endpoint:**
```
http://localhost:8180/realms/incidents-realm/protocol/openid-connect/token
```

**JWKS (public keys) endpoint:**
```
http://localhost:8180/realms/incidents-realm/protocol/openid-connect/certs
```

---

## Project Structure

```
.
├── docker/
│   ├── keycloak/
│   │   └── realm-export.json          # Auto-imported Keycloak realm
│   └── postgres/
│       └── init/
│           └── 01-create-databases.sh # Creates all service databases
├── config-repo/                        # Git-backed config files served by Config Server
│   ├── user-service.yml
│   ├── incident-service.yml
│   └── ...
├── services/
│   ├── config-server/
│   ├── eureka-server/
│   ├── gateway/
│   ├── user-service/
│   ├── incident-service/
│   ├── comment-service/
│   ├── chat-service/
│   └── notification-service/
├── docker-compose.yml
└── .env.example
```

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "feat: add my feature"`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

Please follow [Conventional Commits](https://www.conventionalcommits.org/) for commit messages.

---

> Built with ☕ Spring Boot · 🔐 Keycloak · 🐳 Docker
