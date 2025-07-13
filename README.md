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
I use [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) It creates a single entry point for the application, streamlining API routing, ensuring robust security, monitoring performance, and enhancing resiliency.
![routing](Requirements/Gateway/routing.png)

For example this is how the gateway only allow the requests with the right permisions to access the internal services.

![Flow of auth](Requirements/Gateway/Flow%20of%20auth.png)


# Discovery service
I use [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) It creates a single entry point for the application, streamlining API routing, ensuring robust security, monitoring performance, and enhancing resiliency.
![routing](Requirements/Gateway/routing.png)

For example this is how the gateway only allow the requests with the right permisions to access the internal services.

![Flow of auth](Requirements/Gateway/Flow%20of%20auth.png)



# Config service
I use [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) It creates a single entry point for the application, streamlining API routing, ensuring robust security, monitoring performance, and enhancing resiliency.
![routing](Requirements/Gateway/routing.png)

For example this is how the gateway only allow the requests with the right permisions to access the internal services.

![Flow of auth](Requirements/Gateway/Flow%20of%20auth.png)





# Authentication service 
Implemented authentication in Spring Boot microservices using [Spring Security](https://spring.io/projects/spring-security), [JWT](https://en.wikipedia.org/wiki/JSON_Web_Token). Signup uses event-driven communication to User Management service (via [Kafka](https://kafka.apache.org/)). Signin returns refresh token in HTTP-only cookie and access token in headers; supports secure logout and token refresh endpoint. Optimized with stateless sessions and cookie flags (HttpOnly, Secure, SameSite).

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
