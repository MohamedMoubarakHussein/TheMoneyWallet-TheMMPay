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


