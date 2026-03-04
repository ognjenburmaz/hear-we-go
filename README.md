# Microservices Music Streaming Platform

A robust, event-driven microservices application for streaming music, managing playlists, and social interactions, modeled after Spotify.

This project demonstrates advanced Service-Oriented Architecture (SOA) patterns including Event Sourcing, CQRS, Saga Pattern, Distributed Tracing, and Resilience mechanisms.

---

## Architecture Overview

The system is composed of 8 Java Spring Boot Microservices, an Angular Frontend, and a complex infrastructure mesh running in Docker.

### Services

| Service | Tech Stack | Responsibility |
| :--- | :--- | :--- |
| **Frontend** | Angular 17 + Nginx | SPA User Interface |
| **API Gateway** | Spring Cloud Gateway | Entry point, SSL termination, Rate Limiting (DoS protection) |
| **Discovery** | Netflix Eureka | Service Registry & Load Balancing |
| **Users** | MongoDB | AuthN (JWT), Registration, Profiles |
| **Content** | MongoDB + HDFS | Artist/Album metadata & Audio Blob storage |
| **Ratings** | ScyllaDB (Cassandra) | Song ratings, synchronous validation (gRPC) |
| **Subscriptions** | ScyllaDB (Cassandra) | User follows (Artists/Genres), CQRS logic |
| **Notifications** | ScyllaDB + WebSocket | Real-time alerts via Kafka |
| **Recommendations** | Neo4j (Graph DB) | Personalized content feed |
| **Analytics** | MongoDB | Event-sourced statistics |

### Infrastructure

*   **Message Broker:** Redpanda (Kafka API)
*   **Cache/Rate Limit:** Redis
*   **Tracing:** Jaeger
*   **Blob Storage:** Hadoop HDFS (Namenode + Datanode)

---

## Prerequisites

Before running the project, ensure you have the following installed:

1.  **Docker Desktop** (Allocated at least 6GB RAM and 4 CPUs).
2.  **Java 17/21** (Required for local development/compilation).
3.  **mkcert** (Required for SSL Certificate generation).
    *   Windows (Chocolatey): choco install mkcert
    *   Mac/Linux: brew install mkcert OR sudo apt install mkcert

---

## Configuration & Setup (Crucial Steps)

You must perform these two steps before running Docker, or the services will crash due to missing secrets/certificates.

### 1. Generate SSL Certificates

To satisfy Requirement 2.19 (HTTPS), the entire mesh communicates over SSL. We need to generate a trusted self-signed certificate for localhost and internal Docker hostnames.

Run the provided script in the project root:

For Windows (Git Bash) or Linux/Mac:
sh generate-ssl.sh

Note: This will install a local CA using mkcert and generate a "certs/keystore.p12" file. Docker volumes will mount this file into every container.

### 2. Configure Environment Variables

Create a file named ".env" in the root directory of the project.
It is not already provided since it contains sensitive data. To run the app you must populate it with ports, keys, URIs and so on...
A safe for work list of key value pairs may be provided in the repo at a future date.

---

## Running the Application

Once the certificates and .env file are in place, start the system using Docker Compose.

### Build and Start

This command will compile the Java JARs (using multi-stage builds or host volume mapping depending on configuration) and start all containers.

docker-compose up -d --build

### Startup Time

*   First Run: Please wait 2-5 minutes.
*   HDFS: Takes about 60 seconds to exit "Safe Mode" before songs can be uploaded.
*   Eureka: Services may take up to 90 seconds to register and become visible to the Gateway.

---

## Access Points

| Application | URL | Credentials (If applicable) |
| :--- | :--- |:----------------------------|
| **Web App (Frontend)** | https://localhost | Register a new account      |
| **Eureka Dashboard** | https://localhost:8761 | (Eureka GUI)                |
| **Hadoop (HDFS) UI** | http://localhost:9870 | (HDFS GUI)                  |
| **Jaeger Tracing** | http://localhost:16686 | (Jaeger GUI)                |
| **Redpanda Console** | http://localhost:8088 | (Kafka GUI)                 |

SSL Warning: Since the certificate is self-signed, your browser will warn you that the connection is not private. You must click "Advanced" -> "Proceed to localhost (unsafe)".

---

## Kubernetes (K8s) Deployment

The project is designed to be Cloud-Native. Currently, the primary deployment method is Docker Compose for development ease.

Note: A complete automated setup script for Minikube/Kubernetes deployment (converting .env to ConfigMaps and keystore.p12 to K8s Secrets) will be implemented in a future update.

Architecture mapping for future K8s migration:
*   Config: .env maps to K8s ConfigMaps/Secrets.
*   Networking: Docker Networks maps to K8s Services (ClusterIP).
*   Ingress: Nginx maps to K8s Ingress Controller.

---

## Testing & Validation

### 1. Security (DoS Protection)

The API Gateway implements Rate Limiting via Redis.
To test, spam the API using curl `dos-test.sh` provided in the `attacks` folder, before running change the token.

Expected Result: You will receive 429 Too Many Requests after the burst limit is exceeded.

There are also other attack scripts you can test out in the same folder.

### 2. Distributed Tracing

1.  Perform actions on the frontend (Login, Play Song).
2.  Go to Jaeger UI (http://localhost:16686).
3.  Select "api-gateway" or "content-service" to see the full trace path.

---

## Troubleshooting

1.  "Connection Refused" / SSL Handshake Errors:
    *   Ensure "sh generate-ssl.sh" was run successfully.
    *   Ensure "docker-compose.yml" mounts the "./certs" volume.
    *   Restart services to force a fresh handshake: "docker-compose restart".

2.  HDFS Errors ("NameNode is in Safe Mode"):
    *   HDFS needs time to replicate blocks on startup. Wait 30 seconds.
    *   If errors persist, wipe the volumes to reset the cluster state:
        docker-compose down -v
        docker-compose up -d

3.  Services not appearing in Eureka:
    *   Check memory usage. If containers were OOMKilled (Out of Memory), increase Docker Desktop RAM allocation to 6GB+.