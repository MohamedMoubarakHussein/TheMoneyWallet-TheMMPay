# TheMoneyWallet[TheMMPay]
The Money wallet is an app that lets you pay, send,accept payment and keep track of your payment Using the java and java spring framework :)


![flow](Requirements/flow_EventCommuncation.png)

---
# Gateway service
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
<<<<<<< HEAD


# Wallet service 
Implemented Wallet microservice in Spring Boot providing financial account management with secure transaction endpoints (/deposit, /withdraw, /balance, /transfer). Integrated event consumption from Kafka for real-time balance updates. Built with transactional integrity guarantees, optimistic locking, and idempotency controls to prevent duplicate transactions and ensure data consistency.

# Transaction service 
Created Transaction microservice in Spring Boot handling complex financial operations with atomicity guarantees through endpoints (/process, /verify, /reverse). Implemented event-driven architecture using Kafka for transaction event propagation across services. Built with distributed transaction patterns, compensating actions for rollbacks, and comprehensive audit logging for financial compliance.

# History service
Developed History microservice in Spring Boot with comprehensive endpoints (/getactivity, /getbydate, /filter) for tracking user activities and system events. Leveraged Kafka consumers to capture cross-service events for centralized history recording. Implemented efficient querying patterns with pagination and filtering support for optimized performance on large historical datasets.

# Notification service
Built Notification microservice in Spring Boot with multi-channel delivery support (email, SMS, push) through unified REST endpoints (/send, /schedule, /cancel). Implemented Kafka event consumption for triggering notifications based on system events. Designed with template-based message generation, delivery status tracking, and retry mechanisms for reliable notification delivery.



# Wallet service 
Implemented Wallet microservice in Spring Boot providing financial account management with secure transaction endpoints (/deposit, /withdraw, /balance, /transfer). Integrated event consumption from Kafka for real-time balance updates. Built with transactional integrity guarantees, optimistic locking, and idempotency controls to prevent duplicate transactions and ensure data consistency.

# Transaction service 
Created Transaction microservice in Spring Boot handling complex financial operations with atomicity guarantees through endpoints (/process, /verify, /reverse). Implemented event-driven architecture using Kafka for transaction event propagation across services. Built with distributed transaction patterns, compensating actions for rollbacks, and comprehensive audit logging for financial compliance.

# History service
Developed History microservice in Spring Boot with comprehensive endpoints (/getactivity, /getbydate, /filter) for tracking user activities and system events. Leveraged Kafka consumers to capture cross-service events for centralized history recording. Implemented efficient querying patterns with pagination and filtering support for optimized performance on large historical datasets.

# Notification service
Built Notification microservice in Spring Boot with multi-channel delivery support (email, SMS, push) through unified REST endpoints (/send, /schedule, /cancel). Implemented Kafka event consumption for triggering notifications based on system events. Designed with template-based message generation, delivery status tracking, and retry mechanisms for reliable notification delivery.
