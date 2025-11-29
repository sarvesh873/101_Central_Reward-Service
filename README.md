# Central Reward Service

A high-performance, scalable reward management system that processes transactions and generates rewards based on configurable rules with weighted probabilities. Built with Spring Boot and designed for high throughput with features like idempotency, rate limiting, and event-driven architecture.

## 🚀 Features

- **Transaction Processing**: Process transactions with idempotency checks to prevent duplicate rewards
- **Weighted Reward System**: Configurable reward rules with weighted probabilities
- **Multi-tenant Ready**: Supports multiple users with proper isolation
- **RESTful API**: Well-documented OpenAPI 3.0 specification
- **Event-Driven**: Kafka integration for asynchronous event processing
- **Caching**: High-performance caching with Caffeine
- **Containerized**: Ready for Docker and Kubernetes deployment

### Core Components

1. **Reward Rules Engine**
   - Configurable reward tiers with weighted probabilities
   - Support for different reward types (cashback, vouchers, points)
   - Dynamic rule management

2. **Transaction Processor**
   - Idempotent transaction handling
   - Reward generation based on transaction amount and rules
   - Real-time reward calculation

3. **Claim Management**
   - Reward claiming workflow
   - Expiration handling
   - Redemption code generation

4. **Event System**
   - Kafka-based event publishing
   - Asynchronous reward processing
   - Audit trail for all reward operations

### Database Schema

- **Reward**: Stores individual reward instances
- **RewardRule**: Defines reward rules and their probabilities
- **User**: User information (if not using external auth)

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL (or any JPA-compatible database)
- **Caching**: Caffeine
- **Messaging**: Apache Kafka
- **API Documentation**: OpenAPI 3.0
- **Containerization**: Docker
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker and Docker Compose (for local development)
- Kafka (included in docker-compose)
- PostgreSQL (included in docker-compose)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <https://github.com/sarvesh873/101_Central_Reward-Service/>
   cd 101_Central_Reward-Service
   ```

2. **Start dependencies**
   ```bash
   docker-compose up -d
   ```
   This will start:
   - PostgreSQL database
   - Kafka with Zookeeper
   - Kafka UI (available at http://localhost:8086)

3. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the API documentation**
   - Swagger UI: http://localhost:8086/reward-service/swagger-ui.html
   - OpenAPI JSON: http://localhost:8086/reward-service/v3/api-docs

## 📚 API Endpoints

### 1. Process Transaction
- **POST** `/process`
  - Processes a transaction and generates a reward
  - Idempotent operation (duplicate transaction IDs return the same reward)

### 2. Get Reward by ID
- **GET** `/{rewardId}`
  - Retrieves details of a specific reward

### 3. Claim Reward
- **POST** `/{rewardId}/claim`
  - Claims a previously generated reward
  - Returns a redemption code

### 4. Get User Rewards
- **GET** `/user/{userId}`
  - Retrieves paginated list of rewards for a user
  - Supports pagination with `page` and `size` parameters

## 🔧 Configuration

Configuration is managed through `application.yml` with profiles for different environments:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/rewarddb}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

kafka:
  bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
  topic:
    reward-events: reward-events

app:
  rate-limit:
    enabled: true
    capacity: 10
    time-window: 60
    tokens: 10
```

## 🧪 Testing

Run the test suite:

```bash
mvn test
```

### Test Coverage

Key test scenarios:
- Transaction processing with various amounts
- Idempotency checks
- Reward claim workflow
- Edge cases and error conditions
- Rate limiting

## 🚀 Deployment

### Docker

Build and run with Docker:

```bash
docker build -t reward-service .
docker-compose up -d
```

### Kubernetes

Sample deployment manifests are provided in the `k8s/` directory.

## 📈 Monitoring and Logging

- **Actuator Endpoints**:
  - Health: `/actuator/health`
  - Metrics: `/actuator/metrics`
  - Prometheus: `/actuator/prometheus`

- **Logging**:
  - JSON-formatted logs
  - Correlation IDs for request tracing
  - Log levels configurable via environment variables

## 🔒 Security

- Rate limiting to prevent abuse
- Input validation for all API endpoints
- Secure configuration management
- CORS configuration
- CSRF protection

## ✅ TODO

- [ ] Add admin interface for managing reward rules
- [ ] Implement topic and group names through application properties
- [ ] Create admin API endpoints for reward rule management
- [ ] Add authentication and authorization for admin endpoints
- [ ] Implement input validation for reward rule creation/updates
- [ ] Add API documentation for admin endpoints
- [ ] Write unit and integration tests for new features
- [ ] Add monitoring and logging for admin operations

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📫 Contact

For any queries or support, please contact the development team.
