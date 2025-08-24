FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/transaction-service.jar /app/transaction-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "transaction-service.jar"]
