# TheMoneyWallet[TheMMPay]
The Money wallet is an app that lets you pay, send,accept payments and keep track of your payments Using the java and java spring framework :)


![flow](Requirements/myServices.png)


---
# **Architecture Overview**



This project implements a microservices-based, cloud-native architecture with event-driven communication.
It is designed to be scalable, maintainable, and resilient for enterprise-grade backends.

**Microservices**:<ins> [Gateway service ,Discovery service ,Config service ,Authentication service ,User Managment service ,Wallet service ,Transaction service ,History service ,Notification service ,Dashboard service]</ins>.

**Service Discovery**: Managed via Eureka to enable dynamic service registration.

**API Gateway**: A single entry point using Spring Cloud Gateway for routing and load balancing.

**Event-Driven**: Apache Kafka enables asynchronous messaging and decoupled service communication. 

**Centralized Configuration**: Managed through Spring Cloud Config for environment consistency. 

**Security**: Implemented with JWT and Spring Security OAuth2. 

**Persistence Layer**: MySQL for transactional data, Redis for caching, ClickHouse for analytics. 

**Containerization**: All services are containerized using Docker.

**Tech Stack**
---
**Frameworks**:  Spring Boot ,Spring Web ,Spring Security(Authentication and authorization using JWT and OAuth2) , Spring Data JPA ,Spring Boot Actuator.

**Microservices & Cloud**:  Native Spring Cloud Config Server / Client , Spring Cloud Netflix Eureka, Spring Cloud Gateway, Spring Cloud LoadBalancer. 

**Asynchronous Messaging**:  Apache Kafka , Spring Kafka.

**Authentication & Authorization**:  JWT, Spring OAuth2 Client.

**Persistence & Caching**:  MySQL, Redis , ClickHouse 

**Development & Testing Tools**: Spring Boot DevTools, Spring Boot Starter Test.


# Gateway service
This project uses [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) It creates a single entry point for the application, streamlining API routing, ensuring robust security, monitoring performance, and enhancing resiliency.
![routing](Requirements/Gateway/routing.png)

For example this is how the gateway only allow the requests with the right permisions to access the internal services.

![Flow of auth](Requirements/Gateway/Flow%20of%20auth.png)


# Discovery service

This project uses [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix) for service discovery, allowing microservices to register and locate each other dynamically. It eliminates hardcoded service URLs, supports client-side load balancing, and improves scalability and fault tolerance in a distributed architecture.

![discovery_service flow ](Requirements/Discovery_Service/discovery_service.png)


# Config service

This project uses  [Spring Cloud Config](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/) To externalize and centralize configuration management across microservices, ensuring consistency, reducing duplication, and enabling dynamic updates without redeployments. This improves scalability, maintainability, and environment-specific flexibility.

![config_service flow ](Requirements/Config_service/configService.png)

# Authentication service 

This service handles authentication and authorization for the microservices ecosystem using [Spring Security](https://spring.io/projects/spring-security), [JWT](https://en.wikipedia.org/wiki/JSON_Web_Token), [OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2) standards. It provides endpoints like : ***/signup, /signin, /refreshtoken, /logout, /forgotpassword, /verifytoken, and /verifyemail*** . Each critical action publishes domain events via [Kafka](https://kafka.apache.org/) (UserRegistered, UserLoggedIn, TokenRefreshed, etc.), enabling other services to react asynchronously and remain loosely coupled. Centralizing auth improves security, consistency, and token lifecycle control, while keeping each microservice focused on its domain logic.This modular design simplifies future scaling, auditing, and policy enforcement in distributed systems.

Example of auth flow.
![Auth flow](Requirements/Authentication%20service/sys%20arch%20diagram.png)

# User Managment service 
Developed a User Management microservice in Spring Boot with REST endpoints for complete user lifecycle operations (/signup, /updateuser, /getbyemail, etc.). Implemented event-driven architecture using Kafka for consuming UserSignUpEvent and producing events to other services. Built with a layered architecture (Controller, Service, Repository) following microservices best practices for scalability and maintainability.
![Requirements/User%20Management%20service/arch%20diagram.png](https://github.com/MohamedMoubarakHussein/TheMoneyWallet-TheMMPay/blob/main/Requirements/User%20Management%20%20service/arch%20diagram.png)

# Wallet service 
Implemented Wallet microservice in Spring Boot providing financial account management with secure transaction endpoints (/deposit, /withdraw, /balance, /transfer). Integrated event consumption from Kafka for real-time balance updates. Built with transactional integrity guarantees, optimistic locking, and idempotency controls to prevent duplicate transactions and ensure data consistency.

# Transaction service 
Created Transaction microservice in Spring Boot handling complex financial operations with atomicity guarantees through endpoints (/process, /verify, /reverse). Implemented event-driven architecture using Kafka for transaction event propagation across services. Built with distributed transaction patterns, compensating actions for rollbacks, and comprehensive audit logging for financial compliance.

# History service
Developed History microservice in Spring Boot with comprehensive endpoints (/getactivity, /getbydate, /filter) for tracking user activities and system events. Leveraged Kafka consumers to capture cross-service events for centralized history recording. Implemented efficient querying patterns with pagination and filtering support for optimized performance on large historical datasets.

# Notification service
Built Notification microservice in Spring Boot with multi-channel delivery support (email, SMS, push) through unified REST endpoints (/send, /schedule, /cancel). Implemented Kafka event consumption for triggering notifications based on system events. Designed with template-based message generation, delivery status tracking, and retry mechanisms for reliable notification delivery.


# Dashboard service
Built Notification microservice in Spring Boot with multi-channel delivery support (email, SMS, push) through unified REST endpoints (/send, /schedule, /cancel). Implemented Kafka event consumption for triggering notifications based on system events. Designed with template-based message generation, delivery status tracking, and retry mechanisms for reliable notification delivery.
