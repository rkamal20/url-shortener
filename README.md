# 🚀 URL Shortener

A **production-grade URL shortener** built with **Java, Spring Boot, PostgreSQL and Redis** featuring **custom aliases, analytics, expiration support, and Redis caching**.

---

## ✨ Features

- Short URL generation
- Custom alias support (**6-character alphanumeric**)
- Redis cache-aside strategy for low-latency redirects
- URL analytics
- URL expiration support
- Centralized exception handling
- Request validation
- Interactive Swagger API docs

---

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot**
- **PostgreSQL**
- **Redis**
- **Spring Data JPA**
- **Hibernate**
- **ModelMapper**
- **Swagger / OpenAPI 3**
- **Maven**

---

## 🏗 Architecture

```text
Controller   → API Layer
Service      → Business Logic
Repository   → Data Access Layer
DTO          → Request / Response Models
Entity       → Database Mapping
Exception    → Global Error Handling
Config       → Bean Configuration
```

---

## 📌 API Endpoints

### 1️⃣ Create Short URL

**POST** `/api/urls/shorten`

#### Request

```json
{
  "originalUrl": "https://google.com",
  "customAlias": "kamal1",
  "expiresAt": "2026-12-31T23:59:59"
}
```

#### Response

```json
{
  "shortUrl": "http://localhost:8080/api/urls/r/kamal1"
}
```

---

### 2️⃣ Redirect

**GET** `/api/urls/r/{shortCode}`

Redirects to original URL and increments click count.

---

### 3️⃣ Analytics

**GET** `/api/urls/analytics/{shortCode}`

#### Response

```json
{
  "originalUrl": "https://google.com",
  "shortUrl": "http://localhost:8080/api/urls/r/kamal1",
  "clickCount": 10,
  "createdAt": "2026-05-26T10:00:00",
  "expiresAt": "2026-12-31T23:59:59"
}
```

---

## ⚡ Cache Flow

```text
Request → Redis Cache → Cache Miss → PostgreSQL → Cache Store → Response
```

Improves redirect speed and reduces database load.

---

## ❌ Error Response

```json
{
  "timestamp": "2026-05-26T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid URL format"
}
```

---

## ▶️ Run Locally

### Clone Repository

```bash
git clone https://github.com/rkamal20/url-shortener.git
cd url-shortener
```

### Run Application

```bash
mvn spring-boot:run
```

### Swagger UI

```text
http://localhost:8080/api/swagger-ui/index.html
```

---

## 🔮 Future Improvements

- Redis TTL sync
- Async click counting
- Cloud deployment
- Distributed short-code generation

---

## 👨‍💻 Author

**Kamal Kant Rajput**