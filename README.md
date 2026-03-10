# Order Service - RabbitMQ Microservices Demo

## 📋 Overview

This project demonstrates a **microservices architecture** using **Spring Boot 4.0.3** and **RabbitMQ** for asynchronous communication. It simulates an order processing system where an order service publishes messages to multiple consumer services.

---

# 🏗️ Architecture

```
┌─────────────────┐     ┌──────────────┐     ┌──────────────────┐
│                 │     │              │     │                  │
│  Order Service  │────▶│   RabbitMQ   │────▶│  Email Service   │
│    (Port 8080)  │     │  (Port 5672) │     │    (Port 8082)   │
│                 │     │  Management  │     │                  │
│                 │     │  (Port 15672)│────▶├──────────────────┤
└─────────────────┘     └──────────────┘     │                  │
                                              │  Stock Service   │
                                              │    (Port 8081)   │
                                              │                  │
                                              └──────────────────┘
```

---

## Components

| Service | Port | Responsibility |
|------|------|------|
| **Order Service** | `8080` | Receives order requests and publishes to RabbitMQ |
| **Stock Service** | `8081` | Consumes orders from stock queue and processes inventory |
| **Email Service** | `8082` | Consumes orders from email queue and sends notifications |
| **RabbitMQ** | `5672` (AMQP) / `15672` (Management) | Message broker |

---

# 🚀 Technologies

- Java 17
- Spring Boot 4.0.3
- Spring AMQP / RabbitMQ
- Maven
- Docker

---

# ⚙️ Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker (for RabbitMQ)
- curl or Postman (for testing)

---

# 🐳 Running RabbitMQ with Docker

```bash
docker run -it --rm --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:4-management
```

### RabbitMQ Credentials

- **Username:** `guest`
- **Password:** `guest`
- **Management Console:** http://localhost:15672

---

# 🔧 Configuration

## Order Service (application.properties)

```properties
spring.application.name=order-service
server.port=8080

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

rabbitmq.exchange.name=order_exchange
rabbitmq.queue.stock.name=stock
rabbitmq.binding.stock.routing.key=stock_routing_key
rabbitmq.queue.email.name=email
rabbitmq.binding.email.routing.key=email_routing_key
```

---

## Stock Service (application.properties)

```properties
spring.application.name=stock-service
server.port=8081

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

rabbitmq.exchange.name=order_exchange
rabbitmq.queue.stock.name=stock
rabbitmq.binding.stock.routing.key=stock_routing_key
```

---

## Email Service (application.properties)

```properties
spring.application.name=email-service
server.port=8082

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

rabbitmq.exchange.name=order_exchange
rabbitmq.queue.email.name=email
rabbitmq.binding.email.routing.key=email_routing_key
```

---

# 📦 Project Structure

```
order-service/
├── src/main/java/com/trinhntm/orderservice/
│
├── OrderServiceApplication.java
│
├── config/
│   └── RabbitMQConfig.java
│
├── controller/
│   └── OrderController.java
│
├── dto/
│   ├── Order.java
│   └── OrderEvent.java
│
├── publisher/
│   └── OrderProducer.java
│
└── src/main/resources/
    └── application.properties
```

---

# 🎯 How It Works

1. Client sends a **POST request** to:

```
http://localhost:8080/api/v1/orders
```

2. **Order Service**
- Creates an `OrderEvent`
- Status = `PENDING`

3. **Order Service publishes to RabbitMQ**

Exchange:

```
order_exchange
```

Routing keys:

| Routing Key | Queue |
|------|------|
| `stock_routing_key` | `stock` |
| `email_routing_key` | `email` |

4. **Stock Service**
- Consumes `stock` queue
- Processes inventory

5. **Email Service**
- Consumes `email` queue
- Sends notification email

---

# 📝 API Endpoints

## Place an Order

```
POST http://localhost:8080/api/v1/orders
```

Body:

```json
{
  "name": "Sample Order",
  "quantity": 10,
  "price": 99.99
}
```

Response:

```
Order sent to RabbitMQ ...
```

---

# Check RabbitMQ Console

URL:

```
http://localhost:15672
```

Login:

```
guest / guest
```

Queues:

```
stock
email
```

---

# 🚀 Running the Application

## 1 Start RabbitMQ

```bash
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management
```

---

## 2 Start Order Service

```bash
cd order-service
mvn spring-boot:run
```

---

## 3 Start Stock Service

```bash
cd ../stock-service
mvn spring-boot:run
```

---

## 4 Start Email Service

```bash
cd ../email-service
mvn spring-boot:run
```

---

## 5 Test the Flow

```bash
curl --location 'http://localhost:8080/api/v1/orders' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Test Order",
    "quantity": 5,
    "price": 150.00
}'
```

---

# 📊 Monitoring

## RabbitMQ Management Console

You can monitor:

- Queues
- Connections
- Exchanges
- Message rates

Console:

```
http://localhost:15672
```

---

## Service Logs

Each service logs activity:

| Service | Logs |
|------|------|
| Order Service | Order received + message published |
| Stock Service | Inventory processing |
| Email Service | Email sending |

---

# 🔍 Troubleshooting

## Connection Refused

Check RabbitMQ container:

```bash
docker ps | grep rabbitmq
```

---

## Queue Not Found

Check RabbitMQ console:

```
http://localhost:15672
```

Queues should be auto-created by Spring Boot.

---

## API Hanging

Check:

- Stock service running
- Email service running
- RabbitMQ running
- Service logs

---

# Useful Commands

### Check RabbitMQ Status

```bash
docker exec rabbitmq rabbitmqctl status
```

### List Queues

```bash
docker exec rabbitmq rabbitmqctl list_queues
```

### View Logs

```bash
docker logs rabbitmq
```

---

# 📚 Dependencies

```xml
<dependencies>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

</dependencies>
```

---

# 🧪 Testing the Flow

1. Send order
2. Order service publishes message
3. RabbitMQ routes message to queues
4. Stock service processes inventory
5. Email service sends notification

---

# 📈 Key Features

- Asynchronous Communication via RabbitMQ
- Decoupled Microservices
- Scalable Consumers
- Fault Tolerant Messaging
- RabbitMQ Monitoring Dashboard

---

# 📄 License

This project is for **educational purposes** demonstrating **Spring Boot + RabbitMQ microservices communication**.

---

🚀 Happy Coding