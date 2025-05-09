# TheMoneyWallet[TheMMPay]
The Money wallet is an app that lets you pay, send,accept payment and keep track of your payment Using the java and java spring framework :)


![flow](Requirements/flow_EventCommuncation.png)

---
# Gateway service
I use [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) It creates a single entry point for the application, streamlining API routing, ensuring robust security, monitoring performance, and enhancing resiliency.
![routing](https://github.com/user-attachments/assets/541ef1b8-d7ee-4e4e-86be-af7b848797cb)

For example this is how the gateway only allow the requests with the right permisions to access the internal services.

![Flow of auth](https://github.com/user-attachments/assets/f3543ce0-ecf6-4bf6-872d-9ac79021ef99)





