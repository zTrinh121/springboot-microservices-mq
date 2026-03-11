# Microservices with AWS SNS/SQS 🚀

## Overview

A microservices system using **AWS SNS/SQS** for asynchronous communication between services.

The system contains **3 services**:

* **Order Service** (`8080`) – Publisher
* **Stock Service** (`8081`) – Consumer
* **Email Service** (`8082`) – Consumer

---

# Architecture 📋

```
[Order Service :8080]
       │
       │ Publish to SNS
       ▼
[SNS Topic: order-events-topic]
       │
   ┌───┴───────────────┐
   │                   │
   ▼                   ▼
[stock-queue]     [email-queue]
   │                   │
   ▼                   ▼
[Stock Service]   [Email Service]
   :8081              :8082
```

---

# Flow

1. **Order Service** receives request → publishes message to **SNS Topic**
2. **SNS Topic** broadcasts message to all subscribed queues
3. **Stock Queue** receives message → Stock Service processes it
4. **Email Queue** receives message → Email Service sends email
5. Messages are automatically deleted after successful processing

---

# Technologies 🛠️

* Java 17
* Spring Boot 3.3.0
* Spring Cloud AWS 3.3.0
* AWS SDK v2
* LocalStack (local AWS emulator)
* Maven
* Lombok

---

# Prerequisites 📋

Install required tools:

```bash
# Java
java -version

# Maven
mvn -version

# Docker
docker --version

# Docker Compose
docker-compose --version

# Python
python --version

# AWS CLI
pip install awscli
```

---

# Setup & Installation ⚙️

## 1. Clone Project

```bash
git clone <your-repo-url>
cd springboot-microservices-mq
```

---

# 2. Start LocalStack

```bash
docker-compose up -d
```

Check health:

```bash
curl http://localhost:4566/_localstack/health
```

---

# 3. Create SNS/SQS Resources

```bash
chmod +x scripts/setup-resources.sh
./scripts/setup-resources.sh
```

Verify resources:

```bash
python -m awscli --endpoint-url=http://localhost:4566 sns list-topics
python -m awscli --endpoint-url=http://localhost:4566 sqs list-queues
python -m awscli --endpoint-url=http://localhost:4566 sns list-subscriptions
```

---

# Configuration

## Order Service

`order-service/src/main/resources/application.properties`

```properties
spring.application.name=order-service
server.port=8080

cloud.aws.region.static=us-east-1
cloud.aws.credentials.access-key=test
cloud.aws.credentials.secret-key=test
cloud.aws.sns.endpoint=http://localhost:4566

sns.topic.name=order-events-topic
```

---

## Stock Service

`stock-service/src/main/resources/application.properties`

```properties
spring.application.name=stock-service
server.port=8081

cloud.aws.region.static=us-east-1
cloud.aws.credentials.access-key=test
cloud.aws.credentials.secret-key=test
cloud.aws.sqs.endpoint=http://localhost:4566

sqs.queue.stock=stock-queue
```

---

## Email Service

`email-service/src/main/resources/application.properties`

```properties
spring.application.name=email-service
server.port=8082

cloud.aws.region.static=us-east-1
cloud.aws.credentials.access-key=test
cloud.aws.credentials.secret-key=test
cloud.aws.sqs.endpoint=http://localhost:4566

sqs.queue.email=email-queue
```

---

# Build & Run 🚀

### Terminal 1

```bash
cd stock-service
mvn clean spring-boot:run
```

### Terminal 2

```bash
cd email-service
mvn clean spring-boot:run
```

### Terminal 3

```bash
cd order-service
mvn clean spring-boot:run
```

---

# API Endpoints 🌐

## Create Order

```
POST http://localhost:8080/api/v1/orders
```

### Request Body

```json
{
  "name": "Product Name",
  "quantity": 5,
  "price": 750
}
```

### Response

```
Order published to SNS successfully!
Order ID: uuid-1234-5678
```

---

# Testing 🧪

## Send Test Order

```bash
curl --location 'http://localhost:8080/api/v1/orders' \
--header 'Content-Type: application/json' \
--data '{
"name": "Test Product",
"quantity": 3,
"price": 999
}'
```

---

# Monitor Logs

## Stock Service

```
========== STOCK SERVICE RECEIVED ==========
Order ID: uuid-1234
Stock updated successfully
```

## Email Service

```
========== EMAIL SERVICE RECEIVED ==========
Order ID: uuid-1234
Email sent successfully
```

---

# Check Queue Status

Check messages:

```bash
python -m awscli --endpoint-url=http://localhost:4566 sqs get-queue-attributes \
--queue-url http://sqs.us-east-1.localhost:4566/000000000000/stock-queue \
--attribute-names ApproximateNumberOfMessages
```

Receive messages:

```bash
python -m awscli --endpoint-url=http://localhost:4566 sqs receive-message \
--queue-url http://sqs.us-east-1.localhost:4566/000000000000/stock-queue \
--max-number-of-messages 5
```

---

# Troubleshooting 🔧

## Subscriptions Missing

```bash
python -m awscli --endpoint-url=http://localhost:4566 sns list-subscriptions
```

Create subscription again:

```bash
python -m awscli --endpoint-url=http://localhost:4566 sns subscribe \
--topic-arn arn:aws:sns:us-east-1:000000000000:order-events-topic \
--protocol sqs \
--notification-endpoint arn:aws:sqs:us-east-1:000000000000:stock-queue
```

---

# LocalStack Issues

Restart:

```bash
docker-compose restart
```

View logs:

```bash
docker logs localstack -f
```

---

# Project Structure 📁

```
springboot-microservices-mq/
│
├── order-service
│   ├── controller
│   ├── publisher
│   ├── dto
│   └── pom.xml
│
├── stock-service
│   ├── consumer
│   ├── dto
│   └── pom.xml
│
├── email-service
│   ├── consumer
│   ├── dto
│   └── pom.xml
│
├── scripts
│   ├── setup-resources.sh
│
├── docker-compose.yml
└── README.md
```

---

# Key Features ✨

* Asynchronous communication
* Decoupled microservices
* Parallel message processing
* Fault tolerance with message queues
* Scalable architecture
* Local development using LocalStack

---

# Production Improvements 🚀

1. Replace LocalStack with real AWS
2. Add Dead Letter Queue (DLQ)
3. Add CloudWatch monitoring
4. Add database per service
5. Add API Gateway
6. Dockerize each service

Example DLQ:

```bash
aws sqs create-queue --queue-name stock-dlq
aws sqs create-queue --queue-name email-dlq
```

---

# Author ✍️

**Trinh Nguyen**

---

# Quick Commands

Start environment:

```bash
docker-compose up -d
./scripts/setup-resources.sh
```

Test flow:

```bash
curl -X POST http://localhost:8080/api/v1/orders \
-H "Content-Type: application/json" \
-d '{"name":"Test","quantity":1,"price":100}'
```

Check queue:

```bash
python -m awscli --endpoint-url=http://localhost:4566 sqs get-queue-attributes \
--queue-url http://sqs.us-east-1.localhost:4566/000000000000/stock-queue \
--attribute-names ApproximateNumberOfMessages
```

---

Happy Coding 🚀
