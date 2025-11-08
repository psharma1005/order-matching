# Concurrent Stock Order-Matching Microservice

A Spring Boot microservice that models a Limit Order Book for simulated stock trading. The service processes concurrent market/limit orders using a thread-pooled execution engine and persists trade events in PostgreSQL. Designed for horizontal scalability and deployed via Docker on AWS.

## Architecture

### Matching Engine
- Implements buy/sell matching logic similar to equity LOB systems
- Uses a custom **ExecutorService thread pool** for concurrent order processing
- Order books stored in-memory with periodic persistence

### Persistence Layer
- **AWS RDS (PostgreSQL)** stores:
  - order books
  - trade executions
  - market depth snapshots
- **AWS S3** used for logs and audit artifacts

### Deployment
- Service containerized with **Docker**
- Deployed on **AWS EC2**, connected to **RDS + S3**
- Horizontally scalable architecture (~10k simulated trades/day)

## Tech Stack
Java, Spring Boot, ExecutorService, Docker, AWS (EC2, RDS, S3), PostgreSQL

## My Contribution
- Designed and implemented the multithreaded matching engine
- Built REST endpoints for order submission and monitoring
- Set up PostgreSQL schema + integration with RDS
- Dockerized and deployed the system to AWS
