#  Scalable Notification System

A **production-style, event-driven notification system** built with **Spring Boot**, **Apache Kafka**, **Redis**, and **MySQL**.
This project demonstrates how real backend systems handle **asynchronous processing, retries, rate limiting, fault tolerance, and observability**.

> It focuses on **system design, reliability, and scalability**, similar to how notifications are built in real-world applications.

---

##  Features

### Core Functionality

* Create notifications via REST APIs
* Asynchronous notification delivery using **Kafka**
* Support for **Email notifications** (extensible to SMS / Push)

### Scalability & Reliability

* **Event-driven architecture** using Kafka producer–consumer model
* **Atomic retry locking** to prevent duplicate processing across multiple workers
* **Configurable retry mechanism** with backoff
* **Dead Letter Queue (DLQ)** for permanently failed notifications

### Safety & Protection

* **Redis-based per-user rate limiting** to prevent notification storms
* **Idempotent APIs** using idempotency keys to safely handle duplicate requests

### Observability

* **Micrometer metrics** + **Spring Boot Actuator**
* Tracks:

  * Notifications created
  * Notifications sent successfully
  * Failed deliveries
  * Rate-limited requests

### Engineering Best Practices

* Clean layered architecture (Controller → Service → Repository)
* Thin controllers, business logic in services
* MySQL as the source of truth with well-designed schema
* Delivery attempt logs for debugging and auditing

---

##  High-Level Architecture

```
Client
  |
  v
REST API (Spring Boot)
  |
  |-- Rate Limiter (Redis)
  |
  v
MySQL (Notification Store)
  |
  v
Kafka Topic (notification.send)
  |
  v
Kafka Consumer (Worker)
  |
  |-- Retry Locking
  |-- Delivery Logs
  |-- DLQ on Permanent Failure
  v
Email Sender
```

---

##  Tech Stack

* **Language:** Java
* **Framework:** Spring Boot
* **Messaging:** Apache Kafka
* **Database:** MySQL
* **Caching / Rate Limiting:** Redis
* **Metrics:** Micrometer + Spring Boot Actuator
* **Containerization:** Docker (Kafka, Redis)

---

##  Modules & Responsibilities

### 1. API Layer

* Accepts notification requests
* Enforces rate limiting
* Ensures idempotency

### 2. Kafka Producer

* Publishes `notificationId` events to Kafka
* Decouples request handling from delivery

### 3. Kafka Consumer (Worker)

* Processes notification events
* Acquires retry lock atomically
* Sends notifications
* Handles retries and failures

### 4. Retry & DLQ Handling

* Retries failed notifications up to `maxRetries`
* Routes permanently failed messages to DLQ

### 5. Observability

* Application-level metrics exposed via `/actuator/metrics`

---

##  Database Schema (Simplified)

### `notifications`

* `id`
* `user_id`
* `type` (EMAIL, IN_APP)
* `content`
* `status` (PENDING, PROCESSING, SENT, FAILED)
* `retry_count`
* `max_retries`
* `idempotency_key`
* `created_at`, `updated_at`

### `notification_delivery_logs`

* `notification_id`
* `attempt_number`
* `status` (SUCCESS, FAILED)
* `error_message`
* `created_at`

---

##  Setup & Run Locally

### 1 Prerequisites

* Java 17+
* Docker & Docker Compose
* MySQL

---

### 2️ Start Kafka & Redis

```bash
docker compose up -d
```

Services started:

* Kafka → `localhost:9092`
* Redis → `localhost:6379`

---

### 3️ Configure Application

Update `application.properties`:

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/notification_system
spring.datasource.username=root
spring.datasource.password=your_password

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# Rate Limiting
notification.rate-limit.max-requests=10
notification.rate-limit.window-seconds=60

# Retry
notification.retry.max-retries=3
notification.retry.interval-ms=120000
```

---

### 4️ Run the Application

```bash
./mvnw spring-boot:run
```

---

##  API Usage

### Create Notification

```http
POST /api/notifications
```

```json
{
  "userId": 1,
  "type": "EMAIL",
  "content": "Your order has been shipped",
  "idempotencyKey": "order-123"
}
```

### Possible Responses

* `201 CREATED` → Notification accepted
* `429 TOO MANY REQUESTS` → Rate limit exceeded

---

##  Metrics

Access metrics:

```
GET /actuator/metrics
```

Key metrics:

* `notifications.created`
* `notifications.sent.success`
* `notifications.sent.failed`
* `notifications.rate_limited`

---

## Failure Handling Examples

* Temporary email failure → retried automatically
* Retry limit exceeded → message sent to **Kafka DLQ**
* Duplicate API request → safely handled via idempotency

---

##  Learning Outcomes

This project demonstrates:

* Event-driven system design
* Kafka-based async processing
* Retry locking & DLQ patterns
* Rate limiting with Redis
* Observability with metrics
* Production-grade backend practices

---

##  Future Improvements

* JSON / Avro-based Kafka payloads
* Push & SMS notification channels
* Prometheus + Grafana dashboards
* Distributed tracing
* Kubernetes deployment

---

## Author

**Priyansh Kumar**
B.Tech Computer Science

---

⭐ If you found this project useful, consider starring the repository!
