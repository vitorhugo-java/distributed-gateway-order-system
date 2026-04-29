# Distributed Order Orchestrator (DOO)

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-brightgreen?style=for-the-badge&logo=springboot)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=for-the-badge&logo=postgresql)
![Gradle](https://img.shields.io/badge/Gradle-8.10-02303A?style=for-the-badge&logo=gradle)

## 🚀 Overview

This project implements a robust, distributed architecture for an Order Management System, built with **Java 21** and **Spring Boot 4**. It adopts a microservices approach, utilizing an Auth Gateway pattern to handle security at the edge. 

A high-performance, distributed ecosystem designed for secure order management. This project features a robust **API Gateway** using a manual **WebClient** proxy pattern to ensure centralized authentication and seamless service orchestration.

Key architectural highlights include:
* **Zero-Trust Edge Security:** Stateless JWT authentication and manual proxy routing via Spring WebFlux `WebClient`.
* **High Performance:** Optimized for cloud-native deployment using **GraalVM Native Image**, significantly reducing startup time and memory footprint.
* **Modern Persistence:** Powered by **PostgreSQL 17**, utilizing time-ordered `UUID v7` for optimal B-Tree index performance and anti-fragmentation.
* **Automated Lifecycle:** Full CI/CD pipeline orchestrated with GitHub Actions for testing, native image building (GHCR), and automated SSH deployments.

---

- JavaDoc + TestReports + Coverage Tests: 
- Production URL:

---

## System Architecture

The system is architected as a set of decoupled microservices that communicate over a private network, ensuring data integrity and security.

![diagram.png](diagram.png)

---

## Getting Started

This project is fully containerized for a one-click deployment experience.

### Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)

### Deployment

1. Clone the repository:
```bash
git clone https://github.com/your-user/distributed-order-orchestrator.git
cd distributed-order-orchestrator
```

2. Start the full stack:
```bash
docker-compose up --build
```

3. Access the services:
- Gateway (Entry Point): http://localhost:8080
- Direct Orders API: http://localhost:8081 (internal use recommended)
- Database: localhost:5432

---

## Technology Stack

- Core: Java 21, Spring Boot 4.0.6
- Security: Spring Security, JWT (JSON Web Token)
- Networking: Spring WebFlux (WebClient) for reactive proxying
- Persistence: Spring Data JPA, Hibernate 7, Flyway Migrations
- Databases: PostgreSQL (Production), H2 (Tests)
- DevOps: Docker, multi-stage Dockerfiles, Docker Compose
- Documentation: SpringDoc OpenAPI (Swagger UI)

---

## Security Overview

We implement stateless authentication using JWT.

1. Login: POST /auth/login returns a token
2. Authorization: Use Authorization: Bearer <token>
3. Validation: Gateway validates signature and expiration (1h)

---

## API Reference

### Auth Gateway (8080)

- POST /auth/login
- ANY /api/orders/**

### Order Management (8081)

- GET /api/orders
- POST /api/orders
- GET /api/orders/{id}
- DELETE /api/orders/{id}

---

## Requests Exemples

---

## Testing Credentials

- Username: admin
- Password: admin123

---

## Architectural Decisions

- Manual Proxying (instead of use Spring Cloud Gateway): WebClient used for fine-grained control over headers and error handling
- Database per Service: isolated databases to enforce shared-nothing architecture
- Multi-Stage Build: optimized Docker images with reduced size and attack surface

---

## Pipelines GitHub Actions


---

## ER Diagrams
